package com.zeyad.usecases.app.presentation.user_detail;

import com.google.gson.annotations.SerializedName;
import com.zeyad.usecases.app.presentation.user_list.User;

import org.parceler.Parcel;

import io.realm.RealmObject;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public class Repository extends RealmObject {
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("full_name")
    String fullName;
    @SerializedName("owner")
    User owner;
    @SerializedName("html_url")
    String htmlUrl;
    @SerializedName("description")
    String description;
    @SerializedName("fork")
    boolean fork;
    @SerializedName("url")
    String url;
    @SerializedName("forks_url")
    String forksUrl;
    @SerializedName("keys_url")
    String keysUrl;
    @SerializedName("collaborators_url")
    String collaboratorsUrl;
    @SerializedName("teams_url")
    String teamsUrl;
    @SerializedName("hooks_url")
    String hooksUrl;
    @SerializedName("issue_events_url")
    String issueEventsUrl;
    @SerializedName("events_url")
    String eventsUrl;
    @SerializedName("assignees_url")
    String assigneesUrl;
    @SerializedName("branches_url")
    String branchesUrl;
    @SerializedName("tags_url")
    String tagsUrl;
    @SerializedName("blobs_url")
    String blobsUrl;
    @SerializedName("git_tags_url")
    String gitTagsUrl;
    @SerializedName("git_refs_url")
    String gitRefsUrl;
    @SerializedName("trees_url")
    String treesUrl;
    @SerializedName("statuses_url")
    String statusesUrl;
    @SerializedName("languages_url")
    String languagesUrl;
    @SerializedName("stargazers_url")
    String stargazersUrl;
    @SerializedName("contributors_url")
    String contributorsUrl;
    @SerializedName("subscribers_url")
    String subscribersUrl;
    @SerializedName("subscription_url")
    String subscriptionUrl;
    @SerializedName("commits_url")
    String commitsUrl;
    @SerializedName("git_commits_url")
    String gitCommitsUrl;
    @SerializedName("comments_url")
    String commentsUrl;
    @SerializedName("issue_comment_url")
    String issueCommentUrl;
    @SerializedName("contents_url")
    String contentsUrl;
    @SerializedName("compare_url")
    String compareUrl;
    @SerializedName("merges_url")
    String mergesUrl;
    @SerializedName("archive_url")
    String archiveUrl;
    @SerializedName("downloads_url")
    String downloadsUrl;
    @SerializedName("issues_url")
    String issuesUrl;
    @SerializedName("pulls_url")
    String pullsUrl;
    @SerializedName("milestones_url")
    String milestonesUrl;
    @SerializedName("notifications_url")
    String notificationsUrl;
    @SerializedName("labels_url")
    String labelsUrl;
    @SerializedName("releases_url")
    String releasesUrl;
    @SerializedName("deployments_url")
    String deploymentsUrl;
    @SerializedName("created_at")
    String createdAt;
    @SerializedName("updated_at")
    String updatedAt;
    @SerializedName("pushed_at")
    String pushedAt;
    @SerializedName("git_url")
    String gitUrl;
    @SerializedName("ssh_url")
    String sshUrl;
    @SerializedName("clone_url")
    String cloneUrl;
    @SerializedName("svn_url")
    String svnUrl;
    @SerializedName("homepage")
    String homepage;
    @SerializedName("size")
    int size;
    @SerializedName("stargazers_count")
    int stargazersCount;
    @SerializedName("watchers_count")
    int watchersCount;
    @SerializedName("language")
    String language;
    @SerializedName("has_issues")
    boolean hasIssues;
    @SerializedName("has_downloads")
    boolean hasDownloads;
    @SerializedName("has_wiki")
    boolean hasWiki;
    @SerializedName("has_pages")
    boolean hasPages;
    @SerializedName("forks_count")
    int forksCount;
    @SerializedName("mirror_url")
    String mirrorUrl;
    @SerializedName("open_issues_count")
    int openIssuesCount;
    @SerializedName("forks")
    int forks;
    @SerializedName("open_issues")
    int openIssues;
    @SerializedName("watchers")
    int watchers;
    @SerializedName("default_branch")
    String defaultBranch;

    public Repository() {
    }

    public static boolean isEmpty(Repository repository) {
        return repository == null || (repository.name == null && repository.fullName == null &&
                repository.owner == null && repository.htmlUrl == null && repository.description == null &&
                repository.url == null && repository.forksUrl == null && repository.keysUrl == null &&
                repository.collaboratorsUrl == null && repository.teamsUrl == null &&
                repository.hooksUrl == null && repository.issueEventsUrl == null && repository.eventsUrl == null &&
                repository.assigneesUrl == null && repository.branchesUrl == null && repository.tagsUrl == null &&
                repository.blobsUrl == null && repository.gitTagsUrl == null && repository.gitRefsUrl == null &&
                repository.treesUrl == null && repository.statusesUrl == null && repository.languagesUrl == null &&
                repository.stargazersUrl == null && repository.contributorsUrl == null &&
                repository.subscribersUrl == null && repository.subscriptionUrl == null &&
                repository.commitsUrl == null && repository.gitCommitsUrl == null &&
                repository.commentsUrl == null && repository.issueCommentUrl == null &&
                repository.contentsUrl == null && repository.compareUrl == null &&
                repository.mergesUrl == null && repository.archiveUrl == null &&
                repository.downloadsUrl == null && repository.issuesUrl == null &&
                repository.pullsUrl == null && repository.milestonesUrl == null &&
                repository.notificationsUrl == null && repository.labelsUrl == null &&
                repository.releasesUrl == null && repository.deploymentsUrl == null &&
                repository.createdAt == null && repository.updatedAt == null && repository.pushedAt == null &&
                repository.gitUrl == null && repository.sshUrl == null && repository.cloneUrl == null &&
                repository.svnUrl == null && repository.homepage == null && repository.language == null &&
                repository.mirrorUrl == null && repository.defaultBranch == null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getForksUrl() {
        return forksUrl;
    }

    public void setForksUrl(String forksUrl) {
        this.forksUrl = forksUrl;
    }

    public String getKeysUrl() {
        return keysUrl;
    }

    public void setKeysUrl(String keysUrl) {
        this.keysUrl = keysUrl;
    }

    public String getCollaboratorsUrl() {
        return collaboratorsUrl;
    }

    public void setCollaboratorsUrl(String collaboratorsUrl) {
        this.collaboratorsUrl = collaboratorsUrl;
    }

    public String getTeamsUrl() {
        return teamsUrl;
    }

    public void setTeamsUrl(String teamsUrl) {
        this.teamsUrl = teamsUrl;
    }

    public String getHooksUrl() {
        return hooksUrl;
    }

    public void setHooksUrl(String hooksUrl) {
        this.hooksUrl = hooksUrl;
    }

    public String getIssueEventsUrl() {
        return issueEventsUrl;
    }

    public void setIssueEventsUrl(String issueEventsUrl) {
        this.issueEventsUrl = issueEventsUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

    public String getAssigneesUrl() {
        return assigneesUrl;
    }

    public void setAssigneesUrl(String assigneesUrl) {
        this.assigneesUrl = assigneesUrl;
    }

    public String getBranchesUrl() {
        return branchesUrl;
    }

    public void setBranchesUrl(String branchesUrl) {
        this.branchesUrl = branchesUrl;
    }

    public String getTagsUrl() {
        return tagsUrl;
    }

    public void setTagsUrl(String tagsUrl) {
        this.tagsUrl = tagsUrl;
    }

    public String getBlobsUrl() {
        return blobsUrl;
    }

    public void setBlobsUrl(String blobsUrl) {
        this.blobsUrl = blobsUrl;
    }

    public String getGitTagsUrl() {
        return gitTagsUrl;
    }

    public void setGitTagsUrl(String gitTagsUrl) {
        this.gitTagsUrl = gitTagsUrl;
    }

    public String getGitRefsUrl() {
        return gitRefsUrl;
    }

    public void setGitRefsUrl(String gitRefsUrl) {
        this.gitRefsUrl = gitRefsUrl;
    }

    public String getTreesUrl() {
        return treesUrl;
    }

    public void setTreesUrl(String treesUrl) {
        this.treesUrl = treesUrl;
    }

    public String getStatusesUrl() {
        return statusesUrl;
    }

    public void setStatusesUrl(String statusesUrl) {
        this.statusesUrl = statusesUrl;
    }

    public String getLanguagesUrl() {
        return languagesUrl;
    }

    public void setLanguagesUrl(String languagesUrl) {
        this.languagesUrl = languagesUrl;
    }

    public String getStargazersUrl() {
        return stargazersUrl;
    }

    public void setStargazersUrl(String stargazersUrl) {
        this.stargazersUrl = stargazersUrl;
    }

    public String getContributorsUrl() {
        return contributorsUrl;
    }

    public void setContributorsUrl(String contributorsUrl) {
        this.contributorsUrl = contributorsUrl;
    }

    public String getSubscribersUrl() {
        return subscribersUrl;
    }

    public void setSubscribersUrl(String subscribersUrl) {
        this.subscribersUrl = subscribersUrl;
    }

    public String getSubscriptionUrl() {
        return subscriptionUrl;
    }

    public void setSubscriptionUrl(String subscriptionUrl) {
        this.subscriptionUrl = subscriptionUrl;
    }

    public String getCommitsUrl() {
        return commitsUrl;
    }

    public void setCommitsUrl(String commitsUrl) {
        this.commitsUrl = commitsUrl;
    }

    public String getGitCommitsUrl() {
        return gitCommitsUrl;
    }

    public void setGitCommitsUrl(String gitCommitsUrl) {
        this.gitCommitsUrl = gitCommitsUrl;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }

    public String getIssueCommentUrl() {
        return issueCommentUrl;
    }

    public void setIssueCommentUrl(String issueCommentUrl) {
        this.issueCommentUrl = issueCommentUrl;
    }

    public String getContentsUrl() {
        return contentsUrl;
    }

    public void setContentsUrl(String contentsUrl) {
        this.contentsUrl = contentsUrl;
    }

    public String getCompareUrl() {
        return compareUrl;
    }

    public void setCompareUrl(String compareUrl) {
        this.compareUrl = compareUrl;
    }

    public String getMergesUrl() {
        return mergesUrl;
    }

    public void setMergesUrl(String mergesUrl) {
        this.mergesUrl = mergesUrl;
    }

    public String getArchiveUrl() {
        return archiveUrl;
    }

    public void setArchiveUrl(String archiveUrl) {
        this.archiveUrl = archiveUrl;
    }

    public String getDownloadsUrl() {
        return downloadsUrl;
    }

    public void setDownloadsUrl(String downloadsUrl) {
        this.downloadsUrl = downloadsUrl;
    }

    public String getIssuesUrl() {
        return issuesUrl;
    }

    public void setIssuesUrl(String issuesUrl) {
        this.issuesUrl = issuesUrl;
    }

    public String getPullsUrl() {
        return pullsUrl;
    }

    public void setPullsUrl(String pullsUrl) {
        this.pullsUrl = pullsUrl;
    }

    public String getMilestonesUrl() {
        return milestonesUrl;
    }

    public void setMilestonesUrl(String milestonesUrl) {
        this.milestonesUrl = milestonesUrl;
    }

    public String getNotificationsUrl() {
        return notificationsUrl;
    }

    public void setNotificationsUrl(String notificationsUrl) {
        this.notificationsUrl = notificationsUrl;
    }

    public String getLabelsUrl() {
        return labelsUrl;
    }

    public void setLabelsUrl(String labelsUrl) {
        this.labelsUrl = labelsUrl;
    }

    public String getReleasesUrl() {
        return releasesUrl;
    }

    public void setReleasesUrl(String releasesUrl) {
        this.releasesUrl = releasesUrl;
    }

    public String getDeploymentsUrl() {
        return deploymentsUrl;
    }

    public void setDeploymentsUrl(String deploymentsUrl) {
        this.deploymentsUrl = deploymentsUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(String pushedAt) {
        this.pushedAt = pushedAt;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public void setSshUrl(String sshUrl) {
        this.sshUrl = sshUrl;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    public String getSvnUrl() {
        return svnUrl;
    }

    public void setSvnUrl(String svnUrl) {
        this.svnUrl = svnUrl;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public void setStargazersCount(int stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public int getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(int watchersCount) {
        this.watchersCount = watchersCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean getHasIssues() {
        return hasIssues;
    }

    public void setHasIssues(boolean hasIssues) {
        this.hasIssues = hasIssues;
    }

    public boolean getHasDownloads() {
        return hasDownloads;
    }

    public void setHasDownloads(boolean hasDownloads) {
        this.hasDownloads = hasDownloads;
    }

    public boolean getHasWiki() {
        return hasWiki;
    }

    public void setHasWiki(boolean hasWiki) {
        this.hasWiki = hasWiki;
    }

    public boolean getHasPages() {
        return hasPages;
    }

    public void setHasPages(boolean hasPages) {
        this.hasPages = hasPages;
    }

    public int getForksCount() {
        return forksCount;
    }

    public void setForksCount(int forksCount) {
        this.forksCount = forksCount;
    }

    public String getMirrorUrl() {
        return mirrorUrl;
    }

    public void setMirrorUrl(String mirrorUrl) {
        this.mirrorUrl = mirrorUrl;
    }

    public int getOpenIssuesCount() {
        return openIssuesCount;
    }

    public void setOpenIssuesCount(int openIssuesCount) {
        this.openIssuesCount = openIssuesCount;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(int openIssues) {
        this.openIssues = openIssues;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }
}
