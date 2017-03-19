package eu.daiad.web.repository.application;

import java.util.List;
import java.util.UUID;

import eu.daiad.web.model.group.Cluster;
import eu.daiad.web.model.group.Group;
import eu.daiad.web.model.group.GroupInfo;
import eu.daiad.web.model.group.GroupMember;
import eu.daiad.web.model.query.EnumClusterType;

public interface IGroupRepository {

    Group getByKey(UUID key);

    Group getByKey(UUID key, boolean includeMembers);

    GroupInfo getGroupInfoByKey(UUID key);

    List<GroupInfo> getUtilityGroupInfo(UUID utilityKey);

    List<Group> getAll(UUID utilityKey);

    List<Group> getGroupsByUtilityKey(UUID utilityKey);

    List<Group> getGroupsByUtilityId(int utilityId);

    List<Group> filterByName(UUID utilityKey, String text);

    List<Group> getUtilities(UUID utilityKey);

    List<Group> getClusters(UUID utilityKey);

    List<Group> getClusterSegmentsByKey(UUID clusterKey);

    List<Group> getClusterSegmentsByName(String clusterName);

    List<Group> getClusterSegmentsByType(EnumClusterType custerType);

    List<Group> getSets();

    List<GroupMember> getGroupMembers(UUID groupKey);

    List<GroupInfo> getMemberGroups(UUID userKey);

    List<UUID> getGroupMemberKeys(UUID groupKey);

    List<UUID> getUtilityByIdMemberKeys(int utilityId);

    List<UUID> getUtilityByKeyMemberKeys(UUID utilityKey);

    void deleteAllClusterByName(String name);

    void createCluster(Cluster cluster);

    void createGroupSet(UUID ownerKey, String name, UUID[] members);

    void deleteGroupSet(UUID groupKey);

}
