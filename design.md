# db

## Entities

user table, role controls perms for now

```
- user
  - id: id
  - email: string
  - password: string
  - role: string
  - isActive: true
  - loginAttempts: number
  - createdOn: timestamp
  - createdBy: userId | null // null or systemUser for manual creation
  - updatedOn: timestamp
  - updatedBy: userId

```

categories for prompts
only admins create/modify these

```
- PromptCategory
  - id: string
  - name: string
  - description: string
  - createdOn: timestamp
  - createdBy: userId
  - updatedOn: timestamp
  - updatedBy: userId

```

keywords to flag prompts with
only admins create/modify these

```
- PolicyKeywords
  - id: string
  - content: string
  - createdOn: timestamp
  - createdBy: userId
  - updatedOn: timestamp
  - updatedBy: userId
```

when a prompt is created and theres
a match to a policy keyword

```
- PromptPolicyMatch
  - id
  - promptId: id
  - policyKeywordId: id
  - createdOn: timestamp
```

check against PolicyKeywords on create/update

```
- Prompt
  - id: id
  - title: string
  - content: string
  - categoryId: id
  - owner: userId
  - visibility: string // public/private
  - policyFlagged: boolean
  - createdOn: timestamp
  - createdBy: userId
  - updatedOn: timestamp
  - updatedBy: userId

```

a chat thread when the user hits "use prompt"
or something

```
- Conversation
  - id
  - title: string // need to identify visually
  - promptId: id // the prompt that was used
  - owner: id
  - createdOn: timestamp
  - createdBy: userId
  - updatedOn: timestamp // new message is an update i suppose?
                         // use for sorting by recent
  - policyFlagged: boolean // mark whole chat if there is a flag

```

```
- ConversationMessage
  - id
  - conversationId: id
  - role: string // user | llm, who sent the message
  - content: string
  - policyFlagged: boolean
  - createdOn

```

When a chat message is sent and there
is a policy keyword match
same as the PromptPolicyMatch basically

```
- MessagePolicyMatch
  - id
  - conversationMessageId: id
  - policyKeywordId: id
  - createdOn: timestamp

```
