package eu.daiad.web.repository.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.daiad.web.domain.admin.DailyCounterEntity;
import eu.daiad.web.domain.application.AccountEntity;
import eu.daiad.web.domain.application.UtilityEntity;
import eu.daiad.web.model.admin.Counter;
import eu.daiad.web.model.group.GroupMemberInfo;
import eu.daiad.web.model.utility.UtilityInfo;
import eu.daiad.web.repository.BaseRepository;

@Repository
@Transactional("applicationTransactionManager")
public class JpaUtilityRepository extends BaseRepository implements IUtilityRepository {

    private final String COUNTER_USER = "user";

    private final String COUNTER_METER = "meter";

    private final String COUNTER_AMPHIRO = "amphiro";

    @PersistenceContext(unitName = "default")
    EntityManager entityManager;

    @PersistenceContext(unitName = "management")
    EntityManager adminEntityManager;

    @Override
    public List<UtilityInfo> getUtilities() {
        TypedQuery<UtilityEntity> utilityQuery = entityManager.createQuery("SELECT u  FROM utility u", UtilityEntity.class)
                        .setFirstResult(0);

        List<UtilityEntity> utilities = utilityQuery.getResultList();
        List<UtilityInfo> utilitiesInfo = new ArrayList<UtilityInfo>();

        for (UtilityEntity utility : utilities) {
            UtilityInfo utilityInfo = new UtilityInfo(utility);
            utilitiesInfo.add(utilityInfo);
        }

        return utilitiesInfo;
    }

    @Override
    public UtilityInfo getUtilityById(int id) {
        TypedQuery<UtilityEntity> utilityQuery = entityManager.createQuery("SELECT u FROM utility u where u.id = :id",
                        UtilityEntity.class).setFirstResult(0);

        utilityQuery.setParameter("id", id);

        return new UtilityInfo(utilityQuery.getSingleResult());
    }

    @Override
    public UtilityInfo getUtilityByKey(UUID key) {
        TypedQuery<UtilityEntity> utilityQuery = entityManager.createQuery("SELECT u FROM utility u where u.key = :key",
                        UtilityEntity.class).setFirstResult(0);

        utilityQuery.setParameter("key", key);

        return new UtilityInfo(utilityQuery.getSingleResult());
    }

    @Override
    public List<GroupMemberInfo> getUtilityMemberInfo(UUID key) {
        String memberQueryString = "SELECT a FROM account a WHERE a.utility.key = :key";

        TypedQuery<AccountEntity> memberQuery = entityManager.createQuery(memberQueryString, AccountEntity.class)
                                                             .setFirstResult(0);
        memberQuery.setParameter("key", key);

        List<GroupMemberInfo> members = new ArrayList<GroupMemberInfo>();
        for (AccountEntity account : memberQuery.getResultList()) {
            members.add(new GroupMemberInfo(account));
        }

        return members;
    }

    @Override
    public Map<String, Counter> getCounters(int utilityId) {
        Map<String, Counter> counters = new HashMap<String, Counter>();

        DateTime start = new DateTime();
        DateTime end = start.minusDays(7);

        TypedQuery<DailyCounterEntity> counterQuery = adminEntityManager.createQuery("select c from daily_counter c "
                        + "where c.utilityId = :utilityId and c.createdOn >= :end and c.createdOn <= :start "
                        + "order by c.name, c.createdOn desc", DailyCounterEntity.class);

        counterQuery.setParameter("utilityId", utilityId);
        counterQuery.setParameter("start", start);
        counterQuery.setParameter("end", end);

        for (DailyCounterEntity entity : counterQuery.getResultList()) {
            switch (entity.getName()) {
                case COUNTER_USER:
                    updateCounter(counters, COUNTER_USER, entity.getValue());
                    break;

                case COUNTER_METER:
                    updateCounter(counters, COUNTER_METER, entity.getValue());
                    break;

                case COUNTER_AMPHIRO:
                    updateCounter(counters, COUNTER_AMPHIRO, entity.getValue());
                    break;
            }
        }

        return counters;
    }

    private void updateCounter(Map<String, Counter> counters, String name, long value) {
        Counter counter = null;

        if (counters.containsKey(name)) {
            counter = counters.get(name);

            counter.setDifference(counter.getValue() - value);
        } else {
            counter = new Counter();

            counter.setName(name);
            counter.setValue(value);
            counter.setDifference(0);

            counters.put(name, counter);
        }
    }
}