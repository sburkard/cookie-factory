package cookiefactory.it.citrus.configuration;

import com.consol.citrus.json.JsonSchemaRepository;
import com.consol.citrus.json.schema.SimpleJsonSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class SchemaConfiguration {

    @Bean
    public SimpleJsonSchema orderSchema() {
        return new SimpleJsonSchema(new ClassPathResource("schema/order-schema.json"));
    }

    @Bean
    public JsonSchemaRepository schemaRepository() {
        JsonSchemaRepository schemaRepository = new JsonSchemaRepository();
        schemaRepository.getSchemas().add(orderSchema());
        return schemaRepository;
    }

}
