# Release Procedure

# TL;DR

####  Wednesday -
- [ ] Congrats! you are the release cop now. The cut is today at **17:00 CET/CEST**, let the team know.
#### Thursday -
- [ ] Check that the release branch was created in [Github](https://github.com/Glovo/glovo-courier-android).
- [ ] Check that a beta release was created in [AppCenter](https://install.appcenter.ms/orgs/Glovoapp).
- [ ] Start manual testing! - [Manual Testing Procedure](https://docs.google.com/forms/d/e/1FAIpQLSdHJ2SMrTFpJ9NMTBeG6pqCpVoCz6SMbRO_XNJgUpDoN5Pzlw/viewform)
    - Make sure you uninstall the app
    - Checkout the release branch and create a `debugProguard` build variant
#### Friday -
- [ ] More manual testing
- [ ] Report found bugs to #gx-android
- [ ] Ask team members for any feature toggle that needs to be updated.
#### Monday -
- [ ] A 07:00, the last commit in the release branch will be released. Monitor the `PublishRelease` job in Jenkins
- [ ] Make sure the release in live in the [PlayStore](https://play.google.com/store/apps/details?id=com.glovoapp.courier)
- [ ] Monitor the app performance:
    - [Datadog](https://app.datadoghq.com/logs/analytics?agg_m=&agg_q=%40headers.glovo-app-version&agg_t=count&cols=core_host%2Ccore_service&from_ts=1580296742032&index=main&live=true&messageDisplay=inline&query=env%3Aproduction+%40headers.glovo-app-platform%3AAndroid+%40headers.glovo-app-type%3Acourier&saved_view=75693&step=86400000&stream_sort=desc&to_ts=1580901542032&top_n=25&viz=timeseries) for # of couriers who updated
    - [Firebase](https://console.firebase.google.com/u/0/project/glover-10bfb/analytics/app/android:com.glovo.courier/latestrelease~2F%3Ft=1580900333939&fpn=716238041317&swu=1&sgu=1&sus=upgraded&params=_r..streamId%3D1402666395&cs=app.m.latestrelease.overview&g=1) Crashlytics for crashes
- [ ] If something goes wrong, stop the release from the [PlayStore](https://play.google.com/store/apps/details?id=com.glovoapp.courier) and create a hotfix branch (see below)
#### Tuesday -
- [ ] If no major incidents occur, increase release to 50% through Jenkins (PublishRelease task)
#### Wednesday -
- [ ] If no major incidents occur, increase release to 100% through Jenkins (PublishRelease task)

# Index

- [Release Procedure](#release-procedure)
    + [Summary](#summary)
    + [Release Commander](#release-commander)
- [Release process steps](#release-process-steps)
  * [Step A: Release branch creation](#step-a-release-branch-creation)
    + [Procedure](#procedure)
    + [Postponing the release branch](#postponing-the-release-branch)
  * [Step B: Manual Testing](#step-b-manual-testing)
    + [Procedure](#procedure-1)
  * [Step C: Bugfixing](#step-c-bugfixing)
    + [Procedure](#procedure-2)
  * [Step D: Feature toggles](#step-d-feature-toggles)
    + [Procedure](#procedure-3)
  * [Step E: Releasing](#step-e-releasing)
    + [Procedure](#procedure-4)
    + [Postponing the release](#postponing-the-release)
  * [Step F: Monitoring](#step-f-monitoring)
    + [Procedure](#procedure-5)
  * [Step G: Merge back](#step-g-merge-back)
    + [Procedure](#procedure-6)
  * [Step H: Hotfix](#step-h-hotfix)
    + [Procedure](#procedure-7)
- [Other topics](#other-topics)
    + [Known issues](#known-issues)
    + [Beta Process](#beta-process)
    + [Feature toggles](#feature-toggles)
    + [Creating Mock Orders](#creating-mock-orders)
    + [Testing release variant](#testing-release-variant)

# Summary

1. Every week, on Wednesday, a new **Release Commander** is named and leads the whole process of testing and publishing the App update.
  * Additionally, all changes to Firebase Remote Config should be reported to the Release Officer and made by him or with his consent. This rule is due to the fact that the number of allowable updates is limited in time.
2. After testing (see Test Steps below) they’re released through [AppCenter](https://install.appcenter.ms/orgs/glovoapp/apps/glover-1/distribution_groups/public) (This is temporary until the migration is to the Play Store is complete) and through [PlayStore](https://play.google.com/store/apps/details?id=com.glovoapp.courier) on Monday as a staged rollout 12:00 CET/CEST time
3. Anything merged into develop after **Wednesday 17:00 CET/CEST** morning will have to wait till next release.

* **Extra duty**: the **Release Commander** will have to take care of reviewing the **#ci-courier-app-android** channel on Slack every morning to check the UI Tests run properly.
  If they failed, the **Release Commander** should report in the same message the causes of the failure.
  It can be that sometimes AWS Device Farm fails for some reason, or a test really detected an issue in our app.
  If there's a real problem in the app, the **Release Commander** needs to notify in **#gx-android** and/or the owner of that feature *to fix it as soon as possible*.

**Notes:**
* Jenkins automation times are currently UTC and not CET/CEST as described in this document
* Please keep the team updated, specially if things go wrong
* If you don't know what to do, ask for help. This shouldn't be a lonely task

### Release Commander

The team decided that we go alphabetically on taking turns on who should be the release officer. The list of release commanders and schedule can be found [in this document](https://docs.google.com/document/d/1efdVkPRHWe0iHhE2VTNCiAeY_Hm7WtROhMzuW9btq78).

# Release process steps

## Step A: Release branch creation
Creation of Release Branch [through Jenkins CI](https://ci.glovoint.com/blue/organizations/jenkins/glovo-courier-android/activity). All changes included in the release will be committed in this branch. If fixes need to be made, they will target this branch.
 * Any new features merged after this time into the development branch won't be included in this release.
 * Please [check pending open Pull Requests](https://github.com/Glovo/glovo-courier-android/pulls?q=is%3Aopen+is%3Apr+label%3Aneeded-in-next-cut) with label `needed-in-next-cut`, talk to respective devs and stop automation if needed
 * Branch name is **release/x.x.x**
 *  You should receive a quick status update regarding on-going projects to make sure that a feature won't be accidentally released. This will also help you with the release notes when releasing.


### Procedure
 1. Log into AppCenter (If you can't access AppCenter, please request access to be invited to the organization on **#gx-android**)
 2. In AppCenter, check that the **Beta Release** has been created. You must also check in [Jenkins CI](https://ci.glovoint.com/blue/organizations/jenkins/glovo-courier-android/activity) that **BetaRelease** task has been triggered and run correctly.
 3. In Github, Make sure the `release/x.x.x` branch was created. Every **Wednesday at 17:00 CET/CEST**, a new Release branch is **automatically** created from **develop**. Also make sure:
    * **bump version** and **lokalise strings** were automatically committed

### Postponing the release branch
* If we decide to postpone the branch creation (e.g. due to necessary changes we want to publish in next release or because previous release's conflicts weren't merged yet) automation on develop branch needs to be cancelled in the following way:
    * Open [jenkins Courier page](https://ci.glovoint.com/blue/organizations/jenkins/glovo-courier-android/activity)
    * Search for develop branch
    * Press "play" button and choose "Disable automation"
      * If a job is currently running, you must wait for it to finish or stop it and re-run it later
    * Press "run" button to confirm
    **Don't forget to enable it again later**

 * If automatic creation fails or was delayed, then the release commander needs to manually run the task to create it.
   * **Manually** - on the **develop** branch name expand list and choose **Run with parameters**, choose Stage **ReleaseBranch** and start build

## Step B: Manual Testing
After the release branch is created, **devs checks it out and build the app through the IDE in order to test it**. Beta version will be used for **Glovo Cares**.
* **IMPORTANT**: Make sure testing is done AT LEAST in the following devices (you can fallback to emulator if you lack one of these devices):
	* Lowest API level supported (current: **21**)
	* Highest API level supported (current: **29**)
	* Some other API level in between

### Procedure
  4. From your local machine, checkout the release branch previously created
  5. Make sure **Build Variant** is debugProguard
  6. On the **emulator / real device(s)**. Uninstall the app from the device, and perform a clean install through Android Studio
  7. Check manually each point of the [Manual Testing Procedure](https://docs.google.com/forms/d/e/1FAIpQLSdHJ2SMrTFpJ9NMTBeG6pqCpVoCz6SMbRO_XNJgUpDoN5Pzlw/viewform) and submit the form.
  8. If any problem is found, **it must be fixed in the release branch**. See [next step](#step-c-bugfixing)
*  **Remember**: revert the changes done to proguard above. They shouldn't be committed

## Step C: Bugfixing
Any bugs found by the Release Commander or other developers on the release should be fixed making a PR targeting the *release branch*.

### Procedure
9. Identify which feature might be causing the bug and if there is a team member with expertise on it. For this you can:
	* Use `git blame` or *Annotate* in Android Studio
	* Ask in **#gx-android**
10. Reach out for the team member(s) that can help you fix the bug, if needed
11. Make sure the fix is written and a PR is open **targeting the release branch**
12. Follow-up on author or reviewers to make sure it's fixed before Wednesday
13. After merging a fix, make sure to test the changes going through [testing procedure in Step B](#step-b-manual-testing)

## Step D: Feature toggles
We have some feature toggles in [Firebase](https://console.firebase.google.com/u/0/project/glover-10bfb/config) which needs to be updated prior or after the release. Other feature toggles will already be enabled for new app releases.  Before releasing double-check with the team if any new feature toggle that needs to be manually switched have been inserted.

### Procedure
 14. Check if any [**Feature Toggles**](https://console.firebase.google.com/u/0/project/glover-10bfb/config) needs to be updated prior or after the release.
 15. If any feature toggles needs to be enabled before release, make sure it's done by the team

## Step E: Releasing
Once everything is ready, it's time to release the new version of the app. Please allocate time in your calendar to focus on this.

### Procedure
  16. **Automatically** - On Monday's 07:00, the last commit in the release branch will be released\*.
  Branch will then be merged back into master and development.
  All of this happens automatically through the execution of the `PublishRelease` stage in Jenkins.
  Note that this will not be triggered if automation is disabled manually or due to any errors in the pipeline.
  * \* After being published to [PlayStore](https://play.google.com/store/apps/details?id=com.glovoapp.courier), it will take some time from Google to review it, usually \~2 hours, that's why we moved it to happen at 7:00.

The staged release to PlayStore works in the following schedule:
- 25% on Monday
- 50% on Tuesday
- 100% on Wednesday

Make sure to check the [PlayStore](https://play.google.com/console/u/0/developers/5652925689338288029/app/4976222307775784486/releases/overview) to make sure new version is released. Also keep an active eye in case the app doesn't get approved on the initial stages of rollout.


### Postponing the release
* If we need to delay the release, we need to cancel automation to prevent it from happening
  * Open [jenkins Courier page](https://ci.glovoint.com/blue/organizations/jenkins/glovo-courier-android/activity)
   * Search for release branch
    * Press "play" button and choose "Disable automation"
    * Press "run" button to confirm
    **Don't forget to enable it again later**

* If release task fails or we cancelled automation, we need to **manually** release. The best time to release is in the morning before 12:00 or in the afternoon between 15:00 and 17:30. These time intervals take into account the number of active couriers in Europe and LATAM
    1. Open [jenkins Courier page](https://ci.glovoint.com/blue/organizations/jenkins/glovo-courier-android/activity)
    2. Search for release branch `release/x.x.x`
    3. Open it, run job `PublishRelease`
    4. After few minutes on ci-courier-app-android we are going to have slack notification about release notes

### Troubleshooting

## Release failed: Automation Disabled

Errors and disabled automation are reported on the channel `ci-courier-app-android`.
Check [Jenkins](https://ci.glovoint.com/blue/organizations/jenkins/glovo-courier-android/activity) job failure reason, fix it if possible and __rerun the job the failed__, do *not* restart the whole branch automation.

NOTE: Every failed build on release branch will disable automation

## Step F: Monitoring
After releasing, Release Commander should keep an eye on monitoring tools to make sure release is working fine and not crashing for couriers.
 *  Check [Know issues section](#known-issues) too.

### Procedure
19. Check monitoring tools (Log using *OneLogin*)
    * [Datadog](https://app.datadoghq.com/logs/analytics?agg_m=&agg_q=%40headers.glovo-app-version&agg_t=count&cols=core_host%2Ccore_service&from_ts=1580296742032&index=main&live=true&messageDisplay=inline&query=env%3Aproduction+%40headers.glovo-app-platform%3AAndroid+%40headers.glovo-app-type%3Acourier&saved_view=75693&step=86400000&stream_sort=desc&to_ts=1580901542032&top_n=25&viz=timeseries) for # of couriers who updated
	* [Firebase](https://console.firebase.google.com/u/0/project/glover-10bfb/analytics/app/android:com.glovo.courier/latestrelease~2F%3Ft=1580900333939&fpn=716238041317&swu=1&sgu=1&sus=upgraded&params=_r..streamId%3D1402666395&cs=app.m.latestrelease.overview&g=1) Crashlytics for crashes
	* Or direct links to releases - remember to switch to current release e.g. `2.30.0`
    	* [Firebase crashlytics](https://console.firebase.google.com/u/0/project/glover-10bfb/crashlytics/app/android:com.glovoapp.courier/issues?state=open&time=last-seven-days&type=crash)
        * [DataDog](https://app.datadoghq.com/logs?cols=log_headers.glovo-app-type%2Clog_headers.glovo-app-version%2Clog_headers.glovo-app-platform%2Clog_user_id&from_ts=1586937621669&index=main&live=true&messageDisplay=inline&query=%40headers.glovo-app-type%3Acourier%20%40headers.glovo-app-platform%3AAndroid%20%40headers.glovo-app-version%3A2.28.0&stream_sort=desc&to_ts=1586941221669)

20. If something is wrong with the release. **Stop it** immediately to prevent further users from downloading it, and follow Hotfix step.
    * Manual stop release from [PlayStore](https://play.google.com/store/apps/details?id=com.glovoapp.courier) (you need to have permissions to do so) or go to [Jenkins](https://ci.glovoint.com/blue/organizations/jenkins/glovo-courier-android/branches) and choose `AbandonRelease` from the latest release branch.
    * Create hotfix branch from `master` -> `HotfixBranch`
    * Prepare fix (PR towards the `hotfix/X.XX.X` branch) and merge it
    * Go to Jenkins, select the hotfix branch `hotfix/X.XX.X` and select `PublishRelease`
    * We now should see the release in [PlayStore](https://play.google.com/store/apps/details?id=com.glovoapp.courier)

## Step G: Merge back
Once released, release changes should be merged to `master` to tag the release properly.
Also, since release might contain bugfixes and commits not present in develop, the changes should be merged back to `develop` to reflect this and include it in further versions.

### Procedure
21. Jenkins will post again on **#ci-courier-app-android** Slack channel about merging back to master.
	  * If no conflicts are found, this should be done automatically. Check `master` for merge commit and release tag
	  * If conflicts arise, you will have to fix them and merge with a PR to `master`
22. Jenkins will try to merge changes back to `develop`
	* If no conflicts are found, this should be done automatically. Check `develop` for merge commit
	* If conflicts arise, Jenkins will create a PR `Release x.x.x conflict resolution to develop` under a branch name `mergeResolve-release/x.x.x`. Solve conflicts locally or using GitHub tool, review and merge.

## Step H: Hotfix
If something goes wrong with the release, a hotfix version will be needed. This is basically a new release under the same version number with incremental subversion (e.g. release `2.62.0` -> hotfix `2.62.1`). The process is pretty much the same as with the original release and involves creating a branch and testing, releasing and monitoring again.
  * It's important that both the team members and stake-holders are aware of problems with a release. This will also enable them to help you deal with the situation
  * Post a message in **#gx-android** to inform android GX team
  * Post a message in **#emergency_couriers** to inform other stakeholders who have direct contact with couriers. A good idea is suggesting them to inform couriers who might have a problem to rollback to previous version.

  * **Important**: Changes included in the hotfix branch have to come FROM the hotfix branch and they don't have to include the `hotfix` prefix.

### Procedure
23. Create hotfix branch:
    * Open [jenkins Courier page](https://ci.glovoint.com/blue/organizations/jenkins/glovo-courier-android/activity)
    * On *master* branch run **HotfixBranch** task
24. Fix issues and create PR targeting `hotfix/xxx` branch
25. Follow same steps as with release branches to release a hotfix branch:
    * Manually [release](#step-e-releasing)
    * [Monitor](#step-f-monitoring) the hotfix release
    * Make sure changes are [Merged back](#step-g-merge-back)


# Other topics
### Known issues
1. Firebase reports number of exception thrown by **kustomersdk**. This is standard behaviour.

    Fatal Exception: java.lang.NullPointerException
    Attempt to invoke virtual method ‘d.f.a.d.f d.f.a.k.d.o()’ on a null object reference
    keyboard_arrow_up
    arrow_right
    com.kustomer.kustomersdk.ViewHolders.KUSUserMessageViewHolder.onBind (KUSUserMessageViewHolder.java:81)
 a ndroidx.recyclerview.widget.RecyclerView$Adapter.onBindViewHolder (RecyclerView.java:7065)
arrow_drop_down
com.android.internal.os.ZygoteInit.main (ZygoteInit.java:987)

### What if publish release task fails?
It might happen for the `PublishRelease` task to fail because of unsolvable conflicts with the `master` branch. This event might occur when
`master` history changes, for example as a result of a hotfix that wasn't properly merged back.

To solve the failure and allow this task to complete, follow these steps:
    * Merge `master` back to the `release` branch, solving the conflicts in the process.
    * Open a PR to `release` and merge the changes.
    * Execute again the task `PublishRelease` from CI.


### Beta Process
See progress here [[DRAFT] GX Android Beta Release Process](https://glovoapp.atlassian.net/wiki/spaces/TECH/pages/1167589496/DRAFT%2BGX%2BAndroid%2BBeta%2BRelease%2BProcess)

### Feature Toggles
Strategy of enabling a new feature
  * PM should provide information for which city start roll out.
  * It should be started for 5% users and x2/x3 after every pick
  * Crashes should be monitored every time number of users increased.
  * When reached 100% region should be expanded. PM should provide information about the way of expansion. The percentage increase and monitoring should be repeated for the new region.

### Creating Mock Orders
 To create Mock orders, head on [business.testglovo.com](https://business.testglovo.com/signup) or [stageadmin3.glovoapp.com](http://stageadmin3.glovoapp.com/) depending on the order type (see [Onboarding document](https://glovoapp.atlassian.net/wiki/spaces/TECH/pages/934248506/Android+Onboarding) in case access is needed)

 On the old admin panel (stage3admin)

 1. Go to couriers tab
 2. Search for yourself (the courier you logged in with)
 3. Click on 'Work Now'
 4. Click ‘Assign’ and Select any order from the list or create a new one
 5. The order should now be visible in the app

### Testing release variant
 It would be nice to use the release build to test (not necessarily to perform all the flows, but at least some of the tests)

 The following would be the steps, replacing step 2 of the manual testing section above. **Currently this does not work as the app fails to login both using test or production credentials**

 1. Manually enable “debuggable” for the release flavour
  * Sign the APK using the `packets.jks` keystore in app/keystore folder)
  * Clear data / Uninstall the app from a physical device
  * Install the app using adb `adb install app/release/glovo-release-[release-version].apk`
