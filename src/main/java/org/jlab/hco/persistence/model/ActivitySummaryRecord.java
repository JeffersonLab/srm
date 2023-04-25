package org.jlab.hco.persistence.model;

import java.math.BigInteger;

/**
 * @author ryans
 */
public class ActivitySummaryRecord {
    private final BigInteger groupId;
    private final String groupName;
    private final int upgradeReadyCount;
    private final int upgradeCheckedCount;
    private final int downgradeCheckedCount;
    private final int downgradeNotReadyCount;
    private final int cascadeCount;
    private final int commentCount;

    public ActivitySummaryRecord(BigInteger groupId, String groupName, int upgradeReadyCount, int upgradeCheckedCount, int downgradeCheckedCount, int downgradeNotReadyCount, int cascadeCount, int commentCount) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.upgradeReadyCount = upgradeReadyCount;
        this.upgradeCheckedCount = upgradeCheckedCount;
        this.downgradeCheckedCount = downgradeCheckedCount;
        this.downgradeNotReadyCount = downgradeNotReadyCount;
        this.cascadeCount = cascadeCount;
        this.commentCount = commentCount;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getUpgradeReadyCount() {
        return upgradeReadyCount;
    }

    public int getUpgradeCheckedCount() {
        return upgradeCheckedCount;
    }

    public int getDowngradeCheckedCount() {
        return downgradeCheckedCount;
    }

    public int getDowngradeNotReadyCount() {
        return downgradeNotReadyCount;
    }

    public int getCascadeCount() {
        return cascadeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }
}
