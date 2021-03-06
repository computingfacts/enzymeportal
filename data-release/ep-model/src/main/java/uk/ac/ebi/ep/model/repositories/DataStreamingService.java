package uk.ac.ebi.ep.model.repositories;

import java.util.stream.Stream;
import org.hibernate.ScrollMode;
import org.hibernate.SessionFactory;
import uk.ac.ebi.ep.model.util.QueryStreamUtil;

/**
 * Service to stream data from a data store using Hibernate SessionFactory
 *
 * @author Joseph <joseph@ebi.ac.uk>
 */
public interface DataStreamingService {

    /**
     *
     * @param <T>
     * @param clazz
     * @param sessionFactory
     * @param query
     * @param batchSize
     * @return
     */
    default <T> Stream<T> streamingService(Class<T> clazz, SessionFactory sessionFactory, String query, int batchSize) {
        
        return QueryStreamUtil.resultStream(clazz, batchSize, sessionFactory
                .openSession()
                .createQuery(query).scroll(ScrollMode.FORWARD_ONLY));
        
    }
    
    default <T> Stream<T> streamingService(Class<T> clazz, SessionFactory sessionFactory, String query, int batchSize, long limit) {
        
        return QueryStreamUtil.resultStream(clazz, batchSize, sessionFactory
                .openSession()
                .createQuery(query).scroll(ScrollMode.FORWARD_ONLY)).limit(limit);
        
    }
    
}
