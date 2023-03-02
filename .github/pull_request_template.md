# WIP
// Remember to add the WIP tag as well. Have you considered opening this PR as Draft instead?

// Remove this section if not applicable

## Story description
// Copy description from the Jira ticket

### Link
// Paste link to ticket

## Summary of changes
// Explain all relevant changes in the implementation

// This is the most important part. The better you explain the changes and the more context you provide, the faster and better the reviewers will review your code.

## Screenshots
// Only if relevant, otherwise, remove section. Remove comments if you use this section.

// In order to add images with the formatted table, you can drag and drop them
// to the PR, so they get uploaded. Once you have the URL for them, you just need to
// reference them.

// Create as many rows as needed

// [Remember to add a designer to validate this UI](https://docs.google.com/document/d/1QSmYt4iYZMLoLZ1pEHjkEMi4JiJVK1BJ21QXjRBedSQ)

| Before                  | After                 |
|-------------------------|-----------------------|
| ![before][ref-before-1] | ![after][ref-after-1] |

[ref-before-1]: http://url_before
[ref-after-1]: http://url_after

// If you only need to add one screenshot and you don't want it to be massive, use the following piece of code. Just adjust the width to your needs.

<img src="IMAGE_URL_HERE" width="400">

## Merging guidelines
When merging, make sure that:

- Merge with **create a squash & merge**.
- If this is a release conflict resolution branch use *Create a merge commit*
- At least two relevant reviewers have approved this PR, ideally including someone that has higher seniority than yours.
- If this PR involves code typically touched by another team, make sure an engineer from that team has reviewed it before merging.
- You've added a release notes file
- You've followed [the guidelines of the project](https://github.com/Trainline/android-guidelines).
- You've followed [the guidelines of Pull Requests](https://trainline.atlassian.net/wiki/spaces/TECH/pages/1142128641/Code+Reviews)
- [You've added a designer to review](https://docs.google.com/document/d/1QSmYt4iYZMLoLZ1pEHjkEMi4JiJVK1BJ21QXjRBedSQ/) if there is any UI change
- If you are not confident enough with the changes, remember that [you can manually run UI tests before merging](../README.md#running-ui-tests).
