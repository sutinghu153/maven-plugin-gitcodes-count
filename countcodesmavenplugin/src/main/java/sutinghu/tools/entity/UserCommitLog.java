package sutinghu.tools.entity;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Date;

/**
 * @data2021/12/15,14:59
 * @authorsutinghu
 */
public class UserCommitLog {

    private String user;

    private Date commitDate;

    private String shortMessage;

    private String version;

    private RevCommit revCommit;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public RevCommit getRevCommit() {
        return revCommit;
    }

    public void setRevCommit(RevCommit revCommit) {
        this.revCommit = revCommit;
    }

    @Override
    public String toString() {
        return "Commit Logs{" +
                "userName='" + user + '\'' +
                ", commit time='" + commitDate + '\'' +
                ", short Message='" + shortMessage + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
