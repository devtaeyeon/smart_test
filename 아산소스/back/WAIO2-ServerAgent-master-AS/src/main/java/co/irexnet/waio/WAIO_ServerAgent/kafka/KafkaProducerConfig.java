package co.irexnet.waio.WAIO_ServerAgent.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemConfigDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Service
@Configuration
@Slf4j
public class KafkaProducerConfig
{

	@Autowired
    DatabaseServiceImpl databaseService;

    @Bean
    public ProducerFactory<String, String> localProducerFactory()
    {
        String strBootstrapServers = "localhost:9092";
        log.debug("Database Bootstrap Server : {}", strBootstrapServers);
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, strBootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> localKafkaTemplate()
    {
        return new KafkaTemplate<>(localProducerFactory());
    }

    @Bean
    public ProducerFactory<String, String> vipProducerFactory()
    {
        SystemConfigDTO systemConfig = databaseService.getSystemConfig();
        String strBootstrapServers = systemConfig.getKfk_addr();
        log.debug("Database Bootstrap Server : {}", strBootstrapServers);
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, strBootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> vipKafkaTemplate()
    {
        return new KafkaTemplate<>(vipProducerFactory());
    }

    @Bean
    public ProducerFactory<String, String> bdProducerFactory()
    {
        SystemConfigDTO systemConfig = databaseService.getSystemConfig();
        String strBootstrapServers = systemConfig.getBd_kfk_addr();
        log.debug("Database Bootstrap Server : {}", strBootstrapServers);
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, strBootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> bdKafkaTemplate()
    {
        return new KafkaTemplate<>(bdProducerFactory());
    }
}
