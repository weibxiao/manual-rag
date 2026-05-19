/**
 * Feel free to use this code, Please don't remove the author and email
 */
package panda.ai.rag.config;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.Vector;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.datastax.oss.driver.api.core.type.codec.ExtraTypeCodecs;


/**
 * @author Weibing Xiao 
 * @email panda.007.ai@gmail.com
 */
@Configuration
public class CassandraConfig {

    @Value("${spring.data.cassandra.contact-points}")
    private String host;

    @Value("${spring.data.cassandra.port}")
    private int port;

    @Value("${spring.data.cassandra.local-datacenter}")
    private String datacenter;

    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspace;

    @Bean
    CqlSession cqlSession() {
        return CqlSession.builder()
        	    .addTypeCodecs(ExtraTypeCodecs.floatVectorToArray(756))
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter(datacenter)
                .withKeyspace(keyspace)
                .build();
    }
    
    // 1. Define the converters (same as before)
    @Bean
    public CassandraCustomConversions cassandraCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new CqlVectorToSpringVectorConverter());
        converters.add(new SpringVectorToCqlVectorConverter());
        return new CassandraCustomConversions(converters);
    }

    // 2. FORCE the auto-configured MappingCassandraConverter to use them
    @Bean
    public MappingCassandraConverter cassandraConverter(CassandraMappingContext mappingContext, 
                                                        CassandraCustomConversions conversions) {
        MappingCassandraConverter converter = new MappingCassandraConverter(mappingContext);
        converter.setCustomConversions(conversions); // This is the missing link
        return converter;
    }

    @ReadingConverter
    public static class CqlVectorToSpringVectorConverter implements Converter<CqlVector<Float>, Vector> {
        @Override
        public Vector convert(CqlVector<Float> source) {
            float[] data = new float[source.size()];
            int i = 0;
            for (Float f : source) {
                data[i++] = f;
            }
            return Vector.of(data);
        }
    }

    @WritingConverter
    public static class SpringVectorToCqlVectorConverter implements Converter<Vector, CqlVector<Float>> {
        @Override
        public CqlVector<Float> convert(Vector source) {
            float[] values = source.toFloatArray(); 
            
            // CqlVector.newInstance accepts a List or an Array of the subtype (Float)
            Float[] boxedValues = new Float[values.length];
            for (int i = 0; i < values.length; i++) {
                boxedValues[i] = values[i];
            }
            
            return CqlVector.newInstance(boxedValues);
        }
    }

}