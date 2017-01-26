package eu.daiad.web.repository.application;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import eu.daiad.web.domain.application.AccountAlertEntity;
import eu.daiad.web.domain.application.AccountEntity;
import eu.daiad.web.model.PagingOptions;
import eu.daiad.web.model.message.Alert;
import eu.daiad.web.model.message.EnumAlertType;

public interface IAccountAlertRepository
{
    AccountAlertEntity findOne(int id);

    int countAll();

    List<AccountAlertEntity> findByAccount(UUID accountKey);

    List<AccountAlertEntity> findByAccount(UUID accountKey, Interval interval);

    List<AccountAlertEntity> findByAccount(UUID accountKey, int minId);

    List<AccountAlertEntity> findByAccount(UUID accountKey, int minId, PagingOptions pagination);

    int countByAccount(UUID accountKey);

    int countByAccount(UUID accountKey, Interval interval);

    int countByAccount(UUID accountKey, int minId);

    List<AccountAlertEntity> findByType(EnumAlertType alertType, UUID utilityKey);

    List<AccountAlertEntity> findByType(EnumAlertType alertType, UUID utilityKey, Interval interval);

    int countByType(EnumAlertType alertType, UUID utilityKey);

    int countByType(EnumAlertType alertType, UUID utilityKey, Interval interval);

    Map<EnumAlertType, Integer> countByType(UUID utilityKey);

    Map<EnumAlertType, Integer> countByType(UUID utilityKey, Interval interval);

    List<AccountAlertEntity> findByAccountAndType(UUID accountKey, EnumAlertType alertType);

    List<AccountAlertEntity> findByAccountAndType(UUID accountKey, EnumAlertType alertType, Interval interval);

    int countByAccountAndType(UUID accountKey, EnumAlertType alertType);

    int countByAccountAndType(UUID accountKey, EnumAlertType alertType, Interval interval);

    AccountAlertEntity create(AccountAlertEntity e);

    AccountAlertEntity createWith(UUID accountKey, Alert.ParameterizedTemplate parameterizedTemplate);

    AccountAlertEntity createWith(AccountEntity account, Alert.ParameterizedTemplate parameterizedTemplate);

    boolean acknowledge(int id, DateTime acknowledged);

    boolean acknowledge(AccountAlertEntity r, DateTime acknowledged);

    boolean acknowledge(UUID accountKey, int id, DateTime acknowledged);

    Alert formatMessage(int id, Locale locale);

    Alert formatMessage(AccountAlertEntity r, Locale locale);

    void delete(int id);

    void delete(AccountAlertEntity e);
}
