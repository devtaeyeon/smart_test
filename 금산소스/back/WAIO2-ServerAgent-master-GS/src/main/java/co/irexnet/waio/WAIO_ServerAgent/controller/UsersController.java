package co.irexnet.waio.WAIO_ServerAgent.controller;

import co.irexnet.waio.WAIO_ServerAgent.dto.AccessTokenDTO;
import co.irexnet.waio.WAIO_ServerAgent.dto.UserDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import co.irexnet.waio.WAIO_ServerAgent.util.PropertiesAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@EnableSwagger2
@Slf4j
public class UsersController
{
    @Autowired
    PropertiesAuthentication propertiesAuthentication;

    @Autowired
    DatabaseServiceImpl databaseService;


    /**
     * 사용자 생성 - 관리자 전용
     * 
     * @param token 토큰
     * @param user  사용자정보
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<String> postUsers(@RequestHeader("X-ACCESS-TOKEN") String token, @RequestBody UserDTO user) throws NoSuchAlgorithmException
    {
        log.debug("Recv postUsers[{}]", user.getUsr_id());
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자 권한인지 확인
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority != CommonValue.ADMIN)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
        }

        if(bFindToken == true)
        {
        	UserDTO userDTO = databaseService.getUserFromUserid(user.getUsr_id());
        	if(userDTO != null) {
    			String strErrorBody = "{\"reason\":\"" + HttpStatus.CONFLICT.getReasonPhrase() + "\"}";
    			return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
        	}else {
                // Insert user
        		int nResult = databaseService.addUser(user);
        		log.debug("Create User[{}], result:[{}]", user.getUsr_id(), nResult);
        		if(nResult > 0)
        		{
        			return new ResponseEntity<>("", HttpStatus.CREATED);
        		}
        		else
        		{
        			String strErrorBody = "{\"reason\":\"" + HttpStatus.CONFLICT.getReasonPhrase() + "\"}";
        			return new ResponseEntity<>(strErrorBody, HttpStatus.CONFLICT);
        		}
        	}
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 특정 사용자 정보 조회
     * 
     * @param token  토큰
     * @param usr_id 사용자 ID
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/users/{usr_id}", method = RequestMethod.GET)
    public ResponseEntity<String> getUsers(@RequestHeader("X-ACCESS-TOKEN") String token, @PathVariable String usr_id)
    {
        log.debug("Recv getUsers[{}]", usr_id);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자/운용자 권한인지 확인
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority == CommonValue.GUEST)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
            usr_id = accessTokenDTO.getUsr_id();
        }

        if(bFindToken == true)
        {
            // get User Info
            UserDTO userDTO = databaseService.getUserFromUserid(usr_id);
            log.debug("getUserFromUserid:[{}], result:[{}]", usr_id, userDTO != null ? 1 : 0);

            if(userDTO != null)
            {
                // Make Response Body
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("usr_id", userDTO.getUsr_id());
                userInfo.put("usr_nm", userDTO.getUsr_nm());
                userInfo.put("usr_pn", userDTO.getUsr_pn());
                userInfo.put("usr_auth", userDTO.getUsr_auth());
                userInfo.put("usr_ti", userDTO.getUsr_ti());

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("users", userInfo);

                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();
                try
                {
                    // ObjectMapper를 통해 JSON 값을 String으로 변환
                    strBody = objectMapper.writeValueAsString(responseBody);
                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 전체 사용자 정보 조회
     * 
     * @param token 토큰
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<String> getAllUsers(@RequestHeader("X-ACCESS-TOKEN") String token)
    {
        log.debug("Recv getAllUsers");
        // Check Access Token
        boolean bFindToken = false;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자/운용자 권한인지 확인
            if(accessTokenDTO.getUsr_auth() == CommonValue.GUEST)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            bFindToken = true;
            // Stop auto login time update
//            long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//            Date expirationDate = new Date();
//            expirationDate.setTime(expirationDate.getTime() + expiredTime);
//            databaseService.modToken(token, expirationDate);
        }

        if(bFindToken == true)
        {
            List<UserDTO> userDTOS = databaseService.getAllUser();
            log.debug("getAllUser, result:[{}]", userDTOS.size());

            if(userDTOS.size() > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("login", userDTOS);

                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();
                try
                {
                    // ObjectMapper를 통해 JSON 값을 String으로 변환
                    strBody = objectMapper.writeValueAsString(responseBody);
                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"Empty user\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 사용자 정보 수정 - 관리자 전용
     * 
     * @param token 토큰
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/users/{usr_id}", method = RequestMethod.PUT)
    public ResponseEntity<String> putUsers(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String usr_id,
            @RequestBody UserDTO user)
    {
        log.debug("Recv putUsers[{}]", usr_id);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority = CommonValue.NONE_AUTHORITY;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자 권한인지 확인
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority != CommonValue.ADMIN)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
        }

        if(bFindToken == true)
        {
            // Update user
            user.setUsr_id(usr_id);
            int nResult = databaseService.modUser(nAuthority, user);
            log.debug("modUser:[{}], result:[{}]", user.getUsr_id(), nResult);
            if(nResult > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("users", user);

                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();
                try
                {
                    // ObjectMapper를 통해 JSON 값을 String으로 변환
                    strBody = objectMapper.writeValueAsString(responseBody);
                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * 사용자 정보 수정 - AppBar에서 접근 (관리자/운용자)
     * 
     * @param token 토큰
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/users/myInfo/{usr_id}", method = RequestMethod.PUT)
    public ResponseEntity<String> putUsersMyInfo(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String usr_id,
            @RequestBody UserDTO user)
    {
        log.debug("Recv putUsers[{}]", usr_id);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority = CommonValue.NONE_AUTHORITY;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자/운용자 권한인지 확인
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority == CommonValue.GUEST)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
        }

        if(bFindToken == true)
        {
            // Update user
            user.setUsr_id(usr_id);
            int nResult = databaseService.modUserMyInfo(nAuthority, user);
            log.debug("modUser:[{}], result:[{}]", user.getUsr_id(), nResult);
            if(nResult > 0)
            {
                // Make Response Body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("users", user);

                String strBody;
                ObjectMapper objectMapper = new ObjectMapper();
                try
                {
                    // ObjectMapper를 통해 JSON 값을 String으로 변환
                    strBody = objectMapper.writeValueAsString(responseBody);
                }
                catch(JsonProcessingException e)
                {
                    String strErrorBody = "{\"reason\":\"JsonProcessing Error\"}";
                    return new ResponseEntity<>(strErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(strBody, HttpStatus.OK);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 사용자 삭제 - 관리자 전용
     * 
     * @param token 토큰
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/users/{usr_id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUsers(@RequestHeader("X-ACCESS-TOKEN") String token, @PathVariable String usr_id)
    {
        log.debug("Recv deleteUsers[{}]", usr_id);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자 권한인지 확인
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority != CommonValue.ADMIN)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
        }

        if(bFindToken == true)
        {
            int nResult = databaseService.delUser(usr_id);
            log.debug("delUser:[{}], result:[{}]", usr_id, nResult);

            if(nResult > 0)
            {
                return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 사용자 비밀번호 변경
     * 
     * @param token 토큰
     * @return ResponseEntity<String> 메시지
     */
    @RequestMapping(value = "/users/pw/{usr_id}", method = RequestMethod.PUT)
    public ResponseEntity<String> putUsersPw(
            @RequestHeader("X-ACCESS-TOKEN") String token,
            @PathVariable String usr_id,
            @RequestBody UserDTO user)
    {
        log.debug("Recv putUserPw[{}]", usr_id);
        // Check Access Token
        boolean bFindToken = false;
        int nAuthority;
        AccessTokenDTO accessTokenDTO = databaseService.getToken(token);
        if(accessTokenDTO != null)
        {
            // 관리자 권한인지 확인
            nAuthority = accessTokenDTO.getUsr_auth();
            if(nAuthority == CommonValue.GUEST)
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.FORBIDDEN.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.FORBIDDEN);
            }
            else if(nAuthority == CommonValue.NONE_AUTHORITY)
            {
                String strErrorBody = "{\"reason\":\"Authority is not set.\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.BAD_REQUEST);
            }
            else
            {
                bFindToken = true;
                // Stop auto login time update
//                long expiredTime = CommonValue.ONE_MINUTE * propertiesAuthentication.getExpiration();
//                Date expirationDate = new Date();
//                expirationDate.setTime(expirationDate.getTime() + expiredTime);
//                databaseService.modToken(token, expirationDate);
            }
        }

        if(bFindToken == true)
        {
            // Update user
            int nResult = 0;
			try {
				nResult = databaseService.modUserPw(usr_id, user.getUsr_pw());
            } catch (NoSuchAlgorithmException e) {
                log.error("Error during update", e);
			}
            log.debug("modUserPw:[{}], result:[{}]", usr_id, nResult);
            if(nResult > 0)
            {
                return new ResponseEntity<>("", HttpStatus.OK);
            }
            else
            {
                String strErrorBody = "{\"reason\":\"" + HttpStatus.NOT_FOUND.getReasonPhrase() + "\"}";
                return new ResponseEntity<>(strErrorBody, HttpStatus.NOT_FOUND);
            }
        }
        else
        {
            String strErrorBody = "{\"reason\":\"" + HttpStatus.UNAUTHORIZED.getReasonPhrase() + "\"}";
            return new ResponseEntity<>(strErrorBody, HttpStatus.UNAUTHORIZED);
        }
    }
}
