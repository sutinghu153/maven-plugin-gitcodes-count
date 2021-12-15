package sutinghu.tools;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import sutinghu.tools.entity.UserCommitLog;
import sutinghu.tools.entity.UserCount;
import sutinghu.tools.utils.DateUtils;
import sutinghu.tools.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Goal which touches a timestamp file.
 *
 * 
 * @phase process-sources
 */
@Mojo(name = "count")
public class MyMojo extends AbstractMojo {

    @Parameter(name = "type",defaultValue = "me")
    private String type;
    @Parameter(name = "startTime",defaultValue = "2020-02-01")
    private String startTime ;
    @Parameter(name = "endTime",defaultValue = "2021-12-01")
    private String endTime ;

    private Git git = null;

    private Repository repository = null;

    private String branch = null;

    private Date start = null;

    private Date end = null;

    private  List<UserCommitLog> commitLogs = null;

    @Override
    public void execute(){

        this.initGit();

        this.parseTime();

        try {
            this.getLogs();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        if (Objects.equals(this.type,"me")) {

            this.filterByUser();

            this.filterByTime();

            List<UserCount> userCountList = new ArrayList<>();

            for (UserCommitLog userCommitLog:commitLogs) {
                UserCount userCount = new UserCount();
                userCount.setUserName(userCommitLog.getUser());
                userCount.setComTime(userCommitLog.getCommitDate());
                userCount.setName(userCommitLog.getShortMessage());
                this.countDiff(userCommitLog.getRevCommit(),userCount);
                userCountList.add(userCount);
            }

            System.out.println("-------------------details--------------------");

            userCountList.forEach(e->{
                System.out.println(e.toString());
            });

            System.out.println("-------------------result--------------------");

            int all = 0;
            int add = 0;
            int sub = 0;

            for (UserCount userCount:userCountList) {
                add += userCount.getAddSize();
                all += userCount.getAllSize();
                sub += userCount.getSubSize();
            }

            System.out.println("allsize:"+all);
            System.out.println("addsize:"+add);
            System.out.println("subsize:"+sub);

        }else if (Objects.equals(this.type,"all")) {

            this.filterByTime();

            Map<String, List<UserCommitLog>> collect = commitLogs.stream().collect(Collectors.groupingBy(UserCommitLog::getUser));

            for (Map.Entry<String, List<UserCommitLog>> map:collect.entrySet()) {

                List<UserCount> userCountList = new ArrayList<>();

                for (UserCommitLog userCommitLog:map.getValue()) {
                    UserCount userCount = new UserCount();
                    userCount.setUserName(userCommitLog.getUser());
                    userCount.setComTime(userCommitLog.getCommitDate());
                    userCount.setName(userCommitLog.getShortMessage());
                    this.countDiff(userCommitLog.getRevCommit(),userCount);
                    userCountList.add(userCount);
                }

                System.out.println("-------------------details--------------------");

                userCountList.forEach(e->{
                    System.out.println(e.toString());
                });

                System.out.println("-------------------result--------------------");

                int all = 0;
                int add = 0;
                int sub = 0;

                for (UserCount userCount:userCountList) {
                    add += userCount.getAddSize();
                    all += userCount.getAllSize();
                    sub += userCount.getSubSize();
                }

                System.out.println("userName:"+map.getKey());
                System.out.println("allsize:"+all);
                System.out.println("addsize:"+add);
                System.out.println("subsize:"+sub);

            }

        }else {
            throw new IllegalArgumentException("Nonexistent type");
        }
    }


    public void filterByUser(){
        String username = this.getUser().get("username");
        commitLogs  = commitLogs.stream().filter(e-> Objects.equals(e.getUser(),username)).collect(Collectors.toList());
    }

    public void filterByTime(){
        commitLogs  = commitLogs.stream().filter(userCommitLog -> {
            if (userCommitLog.getCommitDate() != null){
               return start.before(userCommitLog.getCommitDate()) && end.after(userCommitLog.getCommitDate());
            }else {
                return false;
            }

        }).collect(Collectors.toList());
    }

    public void getLogs() throws IOException, GitAPIException {

        Iterable<RevCommit> commits = git.log().all().call();

        List<UserCommitLog> commitLogs = new ArrayList<>();

        for (RevCommit commit : commits) {
            UserCommitLog userCommitLog  = new UserCommitLog();
            userCommitLog.setUser(commit.getAuthorIdent().getName());
            userCommitLog.setCommitDate(commit.getAuthorIdent().getWhen());
            userCommitLog.setRevCommit(commit);
            userCommitLog.setShortMessage(commit.getShortMessage());
            commitLogs.add(userCommitLog);
        }
        this.commitLogs  = commitLogs;
    }

    public void initGit(){
        try {
            repository = new FileRepositoryBuilder()
                    .setGitDir(Paths.get(FileUtils.getDir(), ".git").toFile())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        git = new Git(repository);

        try {
            branch = git.getRepository().getBranch();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Map<String, String> getUser(){
        Map<String, String> map = new HashMap<>();
        StoredConfig config = git.getRepository().getConfig();
        String username = config.getString("user", null, "name");
        String email = config.getString("user", null, "email");
        map.put("username",username);
        map.put("email",email);
        return map;
    }

    public void parseTime(){

        if (StringUtils.isBlank(this.startTime) || StringUtils.isBlank(this.endTime) ) {
            throw new IllegalArgumentException("Start time and end time cannot be empty");
        }

        this.start = DateUtils.getDateTime(this.startTime);
        this.end = DateUtils.getDateTime(this.endTime);

    }

    public void countDiff(RevCommit commit,UserCount userCount){

        String versionTag= commit.getName();

        try {

            RevWalk walk = new RevWalk(repository);

            ObjectId versionId=repository.resolve(versionTag);

            RevCommit verCommit=walk.parseCommit(versionId);

            List<DiffEntry> diffFix=getChangedFileList(verCommit,this.repository);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DiffFormatter df = new DiffFormatter(out);

            df.setRepository(this.repository);
            int addSize = 0;
            int subSize = 0;
            int allsize = 0;
            for (DiffEntry entry : diffFix) {

                df.format(entry);

                FileHeader fileHeader = df.toFileHeader(entry);
                List<HunkHeader> hunks = (List<HunkHeader>) fileHeader.getHunks();


                for(HunkHeader hunkHeader:hunks){
                    EditList editList = hunkHeader.toEditList();
                    for(Edit edit : editList){
                        subSize += edit.getEndA()-edit.getBeginA();
                        addSize += edit.getEndB()-edit.getBeginB();
                        allsize += edit.getEndA()-edit.getBeginA() + edit.getEndB()-edit.getBeginB();
                    }
                }
                out.reset();
            }

            userCount.setAddSize(addSize);
            userCount.setSubSize(subSize);
            userCount.setAllSize(allsize);
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RevCommit getPrevHash(RevCommit commit, Repository repo)  throws  IOException {

        try (RevWalk walk = new RevWalk(repo)) {
            // Starting point
            walk.markStart(commit);
            int count = 0;
            for (RevCommit rev : walk) {
                // got the previous commit.
                if (count == 1) {
                    return rev;
                }
                count++;
            }
            walk.dispose();
        }
        //Reached end and no previous commits.
        return null;
    }

    public static List<DiffEntry> getChangedFileList(RevCommit revCommit, Repository repo) {

        List<DiffEntry> returnDiffs = null;

        try {
            RevCommit previsouCommit=getPrevHash(revCommit,repo);
            if(previsouCommit==null) {
                return null;
            }

            ObjectId head=revCommit.getTree().getId();

            ObjectId oldHead=previsouCommit.getTree().getId();

            // prepare the two iterators to compute the diff between
            try (ObjectReader reader = repo.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldHead);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, head);

                // finally get the list of changed files
                try (Git git = new Git(repo)) {
                    List<DiffEntry> diffs= git.diff()
                            .setNewTree(newTreeIter)
                            .setOldTree(oldTreeIter)
                            .call();

                    returnDiffs=diffs;

                } catch (GitAPIException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return returnDiffs;
    }

}
