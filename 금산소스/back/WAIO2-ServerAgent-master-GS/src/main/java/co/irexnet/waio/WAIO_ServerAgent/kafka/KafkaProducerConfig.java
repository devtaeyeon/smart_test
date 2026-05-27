package co.irexnet.waio.WAIO_ServerAgent.kafka;

import co.irexnet.waio.WAIO_ServerAgent.dto.SystemConfigDTO;
import co.irexnet.waio.WAIO_ServerAgent.service.DatabaseServiceImpl;
import co.irexnet.waio.WAIO_ServerAgent.util.CommonValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaProducerConfig
{

    @Autowired
    DatabaseServiceImpl databaseService;

//     @Bean
//     public ProducerFactory<String, String> producerFactory()
//     {
//         SystemConfigDTO systemConfig = databaseService.getSystemConfig();
//         String strBootstrapServers = systemConfig.getKfk_addr();
// //        SystemConfigDTO systemConfig = databaseService.getSystemConfig();
// //        String strBootstrapServers =
// //            systemConfig.getAnalysis1_address() + ":" + CommonValue.HADOOP_KAFKA_PORT + "," +
// //            systemConfig.getAnalysis2_address() + ":" + CommonValue.HADOOP_KAFKA_PORT;
// //        String strBootstrapServers = systemConfig.getKafka();
// //    	strBootstrapServers = "192.168.100.126:9092";
//     	strBootstrapServers = "smart1:9092,smart2:9092,smart2:9093";
//         log.debug("Database Bootstrap Server : {}", strBootstrapServers);
//         Map<String, Object> properties = new HashMap<>();
//         properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, strBootstrapServers);
//         properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//         properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//         return new DefaultKafkaProducerFactory<>(properties);
//     }


//     @Bean
//     public KafkaTemplate<String, String> kafkaTemplate()
//     {
//         return new KafkaTemplate<>(producerFactory());
//     }

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
