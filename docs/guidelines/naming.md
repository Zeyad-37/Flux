### Naming

#### Branches naming

We should start our branches with the ticket id in front of the name of the branch. 
If there is no ticket related to the task, create one if possible. 
In case of very small tech change that wouldn't require a ticket, just add "TECH/" in front of the name of the branch 
(we should aim to have a ticket for every thing we do to have a visibility and communicate with the team and product).

For a normal branch:

`{project-name (if it's part of a big project)}/{ticket-id}/{Name-of-the-branch}`

Example:

`challenges/GX-411/past-challenge-details`

In case of the small tech change without ticket:

`TECH/Name-of-the-branch`

In case of a fix, we could can add the `fix/` in front of the issue number:

`fix/GX-666/a-hell-fix` 

#### Pull Requests naming

To allow Jira link the issues with the PRs, we should follow the same naming when creating a Pull Request:

`[{ticket-id}] {Description of the PR}`

Example:

`[GX-5555] Integrate close delivery`

When it's a tech PR without a ticket, the PR name should be:

`[TECH] Name of the PR`