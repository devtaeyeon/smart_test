package co.irexnet.waio.WAIO_ServerAgent.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.InterfaceLoginDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.LoginHistoryDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.UserDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.Conversion;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesAuthentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@EnableSwagger2
@Slf4j
public class LoginController {
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;

    /**
     * 이중화 구조에서 시각화 Health Check를 위해 hostname을 반환
     * 
     * @param request
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/hostname", method = RequestMethod.GET)
    public ResponseEntity<String> getHostname(HttpServletRequest request) {
        log.debug("Receive getHostname");

        try {
            String strBody = InetAddress.getLocalHost().getHostName();
            return new ResponseEntity<>(strBody, HttpStatus.OK);
        } catch (UnknownHostException e) {
            String strErrorBody = "{\"reason\":\"Unknown Host\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 시각화 서비스 로그인
     * 
     * @param interfaceLoginDto Front-end 로그인 정보를 저장하기 위한 DTO
     * @param request
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> postLogin(@RequestBody InterfaceLoginDTO interfaceLoginDto,
            HttpServletRequest request) throws NoSuchAlgorithmException {
        log.debug("Recv postLogin [{}]", interfaceLoginDto.getUsr_id());
        // Get User Information from Database
        UserDTO userDTO = databaseService.getUser(interfaceLoginDto.getUsr_id(),
                Conversion.hashPassword(interfaceLoginDto.getUsr_pw()));
        // UserDTO userDTO = databaseService.getUser(interfaceLoginDto.getUsr_id(),
        // interfaceLoginDto.getUsr_pw());
        log.debug("getUser:[{}], result:[{}]", interfaceLoginDto.getUsr_id(), userDTO != null ? 1 : 0);

        if (userDTO != null) {
            // Create Access Token
            String strToken = "";
            int nAuthority = userDTO.getUsr_auth();

            List<AccessTokenDTO> accessTokenList = databaseService.getAllTokens();
            int nTokenCount = accessTokenList.size();
            log.debug("getAllTokens Token:[{}]", nTokenCount);

            // Create Access Token / Check Duplicate Token
            if (nTokenCount > 0) {
                boolean bDuplicate = true;
                while (bDuplicate == true) {
                    bDuplicate = false;
                    // 관리자일 경우 default, 운용자일 경우 관리자 설정값으로 변경
                    if(nAuthority == CommonValue.ADMIN) {
                    	strToken = createToken(propertiesAuthentication.getKey(), propertiesAuthentication.getExpiration(), interfaceLoginDto.getLocaltime());
                    } else if(nAuthority == CommonValue.USER) {
                    	strToken = createToken(propertiesAuthentication.getKey(), userDTO.getUsr_ti(), interfaceLoginDto.getLocaltime());
                    } else {
                    	String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                        return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                    }
                    for (int i = 0; i < nTokenCount; i++) {
                        if (accessTokenList.get(i).getTkn().equalsIgnoreCase(strToken) == true) {
                            bDuplicate = true;
                            break;
                        }
                    }
                }
            } else {
            	// 관리자일 경우 default, 운용자일 경우 관리자 설정값으로 변경
                if(nAuthority == CommonValue.ADMIN) {
                	strToken = createToken(propertiesAuthentication.getKey(), propertiesAuthentication.getExpiration(), interfaceLoginDto.getLocaltime());
                } else if(nAuthority == CommonValue.USER) {
                	strToken = createToken(propertiesAuthentication.getKey(), userDTO.getUsr_ti(), interfaceLoginDto.getLocaltime());
                } else {
                	String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
                }
            }

            // Set expiration date
//            Date expirationDate = new Date();
            Date expirationDate = interfaceLoginDto.getLocaltime();
            try {
                long exp = getExpirationDateFromJwtString(propertiesAuthentication.getKey(), strToken);
                expirationDate.setTime(exp);
            } catch (InterruptedException e) {
                expirationDate = new Date();
            }

            // Create AccessTokenDTO
            AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
            accessTokenDTO.setUsr_id(userDTO.getUsr_id());
            accessTokenDTO.setUsr_nm(userDTO.getUsr_nm());
            accessTokenDTO.setTkn(strToken);
            accessTokenDTO.setUsr_auth(userDTO.getUsr_auth());
            accessTokenDTO.setExpr_ti(expirationDate);

            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("tkn", accessTokenDTO.getTkn());
            responseBody.put("usr_auth", userDTO.getUsr_auth());
            responseBody.put("expr_ti", accessTokenDTO.getExpr_ti());

            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Insert Access Token
            databaseService.addToken(accessTokenDTO);

            // Insert login_history
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setUsr_id(userDTO.getUsr_id());
            loginHistoryDTO.setUsr_nm(userDTO.getUsr_nm());
            loginHistoryDTO.setLgn_ty(CommonValue.LOGIN);
            loginHistoryDTO.setLgn_ti(new Date());

            // Get Request source address
            if (request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") == true) {
                loginHistoryDTO.setLgn_addr("127.0.0.1");
            } else {
                String strAddress = "";
                Enumeration<String> forwarded = request.getHeaders("X-Forwarded-For");
                while (forwarded.hasMoreElements()) {
                    strAddress = forwarded.nextElement();
                }

                loginHistoryDTO.setLgn_addr(strAddress == "" ? request.getRemoteAddr() : strAddress);
            }
            log.debug("addLoginHistory:[{}], result:[{}]",
                    loginHistoryDTO.getUsr_id(), databaseService.addLoginHistory(loginHistoryDTO));

            return new ResponseEntity<>(strBody, HttpStatus.CREATED);
        } else {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 로그인 시간 연장
     * 
     * @param token 토큰
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "login", method = RequestMethod.PUT)
    public ResponseEntity<String> putLogin(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody InterfaceLoginDTO interfaceLoginDto) {
        log.debug("Recv putLogin");
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority = CommonValue.NONE_AUTHORITY;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        // Check Authority
        if (accessTokenDTO != null) {
            nAuthority = accessTokenDTO.getUsr_auth();
            if (nAuthority == CommonValue.GUEST) {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            } else if (nAuthority == CommonValue.NONE_AUTHORITY) {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            } else {
                // 로그인 시간 연장
                bFindToken = true;
                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
                
                // 운용자일 경우 관리자 설정값으로 변경
                if(nAuthority == CommonValue.USER) {
                	UserDTO user = databaseService.getUserFromUserid(accessTokenDTO.getUsr_id());
                	expiredTime = CommonValue.ONE_MINUTE * user.getUsr_ti();
                }
//                Date expirationDate = new Date();
                Date expirationDate = interfaceLoginDto.getLocaltime();
                expirationDate.setTime(expirationDate.getTime() + expiredTime);
                databaseService.modToken(token, expirationDate);
                accessTokenDTO = databaseService.getToken(token);
            }
        }

        if (bFindToken == true) {
            // Make Response Body
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("tkn", accessTokenDTO.getTkn());
            responseBody.put("usr_auth", nAuthority);
            responseBody.put("expr_ti", accessTokenDTO.getExpr_ti());

            String strBody;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // ObjectMapper를 통해 JSON 값을 String으로 변환
                strBody = objectMapper.writeValueAsString(responseBody);
            } catch (JsonProcessingException e) {
                String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(strBody, HttpStatus.CREATED);
        } else {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 로그인 이력 조회
     * 
     * @param token 토큰
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<String> getLogin(@RequestHeader("X-ACCESS-TOKEN") String token) {
        log.debug("Recv getLogin");
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        // Check Authority
        if (accessTokenDTO != null) {
            nAuthority = accessTokenDTO.getUsr_auth();
            if (nAuthority == CommonValue.GUEST) {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            } else if (nAuthority == CommonValue.NONE_AUTHORITY) {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            } else {
                bFindToken = true;
                // Stop auto login time update
                // long expiredTime = CommonValue.ONE_MINUTE *
                // propertiesAuthentication.getExpiration();
                // Date expirationDate = new Date();
                // expirationDate.setTime(expirationDate.getTime() + expiredTime);
                // databaseService.modToken(token, expirationDate);
            }
        }

        if (bFindToken == true) {
            // get login_history
            List<LoginHistoryDTO> loginHistoryDTOS = databaseService.getAllLoginHistory();
            log.debug("getAllLoginHistory, result:[{}]", loginHistoryDTOS.size());

            if (loginHistoryDTOS.size() > 0) {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("login", loginHistoryDTOS);

                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    // ObjectMapper를 통해 JSON 값을 String으로 변환
                    strBody = objectMapper.writeValueAsString(responseBody);
                } catch (JsonProcessingException e) {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            } else {
                String strErrorBody = "{\"reason\":\"Empty login_history\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        } else {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 로그아웃
     * 
     * @param token   토큰
     * @param request
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteLogout(@RequestHeader("X-ACCESS-TOKEN") String token,
            HttpServletRequest request) {
        log.debug("Recv deleteLogout");
        // Check Access Token
        boolean bFindToken = false;
        String strUserid = "", strName = "";

        // Delete AccessToken
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);

        if (accessTokenDTO != null) {
            strUserid = accessTokenDTO.getUsr_id();
            strName = accessTokenDTO.getUsr_nm();

            databaseService.delToken(token);
            bFindToken = true;
        }

        if (bFindToken == true) {
            // Insert login_history
            LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
            loginHistoryDTO.setUsr_id(strUserid);
            loginHistoryDTO.setUsr_nm(strName);
            loginHistoryDTO.setLgn_ty(CommonValue.LOGOUT);
            loginHistoryDTO.setLgn_ti(new Date());

            // Get request source address
            if (request.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") == true) {
                loginHistoryDTO.setLgn_addr("127.0.0.1");
            } else {
                String strAddress = "";
                Enumeration<String> forwarded = request.getHeaders("X-Forwarded-For");
                while (forwarded.hasMoreElements()) {
                    strAddress = forwarded.nextElement();
                }

                loginHistoryDTO.setLgn_addr(strAddress == "" ? request.getRemoteAddr() : strAddress);
            }
            int nResult = databaseService.addLoginHistory(loginHistoryDTO);

            log.debug("[{}] logout result:[{}]", strUserid, nResult);
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        } else {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 토큰생성
     * 
     * @param key    키
     * @param minute 만료시간
     * @return String 토큰발급
     */
    private static String createToken(String key, long minute, Date localtime) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        long expiredTime = CommonValue.ONE_MINUTE * minute;
//        Date expirationDate = new Date();
        Date expirationDate = localtime;
        expirationDate.setTime(expirationDate.getTime() + expiredTime);
        payloads.put("exp", expirationDate.getTime());
        payloads.put("data", "login success");

        String strJwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();

        return strJwt;
    }

    /**
     * @deprecated 미사용
     * @param key
     * @param jwtTokenString
     * @return String
     * @throws InterruptedException
     */
    private static String getTokenFromJwtString(String key, String jwtTokenString) throws InterruptedException {
        Claims claims = Jwts.parser()
                .setSigningKey(key.getBytes())
                .parseClaimsJws(jwtTokenString)
                .getBody();

        Date expiration = claims.get("exp", Date.class);
        // System.out.println(expiration);

        String data = claims.get("data", String.class);
        // System.out.println(data);

        return data;
    }

    /**
     * 토큰 만료시간 세팅
     * 
     * @param key            키값
     * @param jwtTokenString 토큰
     * @return long 만료시간
     */
    private static long getExpirationDateFromJwtString(String key, String jwtTokenString) throws InterruptedException {
        Claims claims = Jwts.parser()
                .setSigningKey(key.getBytes())
                .parseClaimsJws(jwtTokenString)
                .getBody();

        Date dExpiration = claims.get("exp", Date.class);

        long expiration = dExpiration.getTime() / 1000; // JWT Date get Bug
        // System.out.println(expiration);

        return expiration;
    }
}
