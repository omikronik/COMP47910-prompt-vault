# COMP47910 - Secure Software Engineering 2026

## Yasir Celtik

## How to run

### Database

- I chose to use a docker container in my homelab, if you are on linux/mac you should be able to run
  `COMP47910-prompt-vault/db/run_mysql_container.sh` to run the db container.
- I set the password to 'yourpassword' as default, change this in the script if needed
- set the db connection properties in the application.properties
  ```
  spring.datasource.url=jdbc:mysql://<ip>:3306/promptvault
  spring.datasource.username=promptvault
  spring.datasource.password=yourpassword
  ```
- log in to the database root account and run `create database promptvault` and

### Spring app

- I did not use an IDE so i ran the app with `./mvnw spring-boot:run`
- you will need to figure out how to run it in an IDE if desired
- After the app runs, it will seed the database automatically.
- navigate to `localhost:8080/` to get into the app.

## Assignment 1 - Prompt Vault

In this assignment, you will develop a simplified web application called
**PromptVault** using **Spring Boot.** PromptVault is a secure prompt management
system where users can create, store, organise, and submit prompts to a
simulated AI assistant.
The project focuses on implementing core features such as user registration,
prompt management, simulated AI requests, and reviewing prompts that may contain
sensitive information. The project, at this stage WILL NOT require you to
consider the security aspects of the application, including authentication,
access control, input validation, and protection of sensitive data.
The application does not need to integrate with a real AI service. You can
simulate the AI response by returning a predefined message such as:
“This is a simulated AI response.”

Alternatively, you can generate a simple response based on the submitted prompt.
You are not required to use any external AI API.

### Required Functionalities for Admin

- [x] **Log in and log out of the system.** You can assume that admins are already
      registered with a predefined username and password. So, they will only need
      to perform login and logout operations.
- [x] **Manage users.** After logging in, an admin can view the list of registered
      users. Each user should include username, email address, role, and account
      status. The admin should be able to enable or disable a user account.
- [x] **Manage prompt categories.**
      After logging in, an admin can add, edit, and delete prompt categories. Each
      category should include the category name and description. Examples of
      categories include:
  - Coding;
  - Research;
  - Cybersecurity;
  - Legal;
  - HR;
  - Personal productivity.

- [x] **Manage policy keywords.**
      After logging in, an admin can add, edit, and delete policy keywords. These
      keywords will be used to identify prompts that may contain sensitive
      information. Examples of policy keywords include: password; API key; secret;
      credit card; private key; confidential; medical record; student number.

- [x] **View flagged prompts.**
      After logging in, an admin can view prompts that have been flagged because
      they contain one or more policy keywords. For each flagged prompt, the admin
      should be able to see the prompt title, prompt owner, category, flagged
      keyword, and date of submission.

### **Required Functionalities for Users**

- [x] **Register in PromptVault.**
      Users can register by providing their details: name, surname, username,
      email address, and password.
- [x] **Login and logout of the system.**
      Registered users can perform the login and should be able to log out after
      performing the login.
- [x] **Create a prompt.**
      After logging in, a user can create a prompt. Each prompt should include the
      title, prompt text, category, and visibility status. The visibility status
      can be private or shared.
- [x] **View own prompts.**
      After logging in, a user can view the list of prompts they created. A user
      should only be able to view their own private prompts.
- [x] **Edit and delete own prompts.**
      After logging in, a user can edit and delete prompts they created. A user
      should not be able to edit or delete prompts created by another user.
- [x] **Browse shared prompts.**
      A user can browse prompts marked as shared by other users. Private prompts
      should not be visible to other users.
- [x] **Submit a prompt to the simulated AI assistant.**
      After logging in, a user can submit one of their prompts to the simulated AI
      assistant. The application should display a simulated response. The
      application does not need to contact any external AI service.
- [x] **View prompt submission history.**
      After logging in, a user can view the history of prompts they submitted to
      the simulated AI assistant. The history should include the prompt title,
      submission date, and simulated AI response. A user should only be able to view
      their own submission history.
- [x] **Receive a warning for sensitive prompts.**
      If a submitted prompt contains one or more policy keywords, the application
      should display a warning message to the user. The prompt should also be
      marked as flagged so that it can be reviewed by the admin.
      For example, if a prompt contains the words “password” or “API key”, the
      application should warn the user that the prompt may contain sensitive
      information.

### Technical Requirements

Your web app should be implemented using **Spring Boot** and the **Java**
language. Unfortunately, you won’t have the flexibility to choose your favourite
technology to implement the web app. Your web application requires a database to
include users, login credentials, prompt categories, prompts, policy keywords,
and prompt submission history. You should use **MySQL** as a database management
system.
There are no requirements concerning the graphical user interface of the web
application. You can use Thymeleaf or React. The tutorial material will only
provide an example of how to use Thymeleaf in your web application. The
application does not need to use a real AI API. You can simulate the AI
assistant using a simple predefined response generated by the application.

**Suggested Database Content**
Your database should include enough information to test the application.

At minimum, it should include:

- one predefined admin account;
- at least two registered users;
- at least three prompt categories;
- at least five prompts, including private and shared prompts;
- at least five policy keywords.

You may create this data manually, using SQL scripts, or through the application.

### Submission Requirements

**You should submit:**

- the source code of your Spring Boot project;
- instructions explaining how to run your application;
- the database creation script or migration files;

### Working on Your Assignment

You are required to work on this assignment individually. You can use ChatGPT,
but it is not required. **You are not allowed to use any other Large Language
Model different than ChatGPT.**

If you decide to use ChatGPT, you **MUST** include the complete chat history in your
submission. Please, remember to save the full chat history in a text document.
You must also ask which version of GPT you are using before your interaction.
**Note:** When ChatGPT exceeds the token limit, you will need to refresh the page
and lose the previous chat history. In that situation, please remember to append
the chat in your text document before refreshing the page.\*\*

### Submitting Your Assignment

To submit your assignment, you need to provide:
The implemented WebApp by providing a link to the distributed version repository
(Git) or submitting the Zip version of your project. If you used ChatGPT, you
will need to fill out a [questionnaire](https://docs.google.com/forms/d/e/1FAIpQLSdvV0EJkMJHY0EV8oyEtqLbwhEMF9SvfDGteu9j8c7qy8n2pA/viewform?usp=sharing&ouid=104321067307543013979) where you will need to provide a brief
description (max 400 words) of how you used ChatGPT and answer multiple-choice
questions about ChatGPT usefulness. If you used ChatGPT, submit a text document
including the complete chat history.

### Evaluation Criteria

You will not be evaluated for how well you have secured your application. Also,
you won’t be penalised for using ChatGPT. The only thing we will look at is the
functionality of your application.

|                     | A                                                                                    | B                                                                                              | C                                                                                             | D                                                                                  | E                                                 |
| ------------------- | ------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------- | ------------------------------------------------- |
| Functionality (75%) | Code runs in the web server and performs correctly all the functionalities requested | Code runs in the web server and performs correctly most of the functionalities requested.      | Code runs in the web server but only performs half of the functionalities requested           | Code runs on the web server but only performs few of the functionalities requested | Code won’t run and the project page won’t display |
| Persistency (20%)   | Data persistence implemented correctly                                               | Data persistence implemented correctly                                                         | Data persistence implemented correctly although entity classes could have improved.           | Data persistence not implemented.                                                  | Data persistence not implemented.                 |
| Look and Feel (5%)  | Facilitates navigability of the application                                          | The graphical interface is easy to use although in some parts navigation is not very intuitive | The graphical interface is not very easy to use and in many parts navigation is not intuitive | The graphical interface is implemented but it is hard to use .                     | No graphical interface                            |
