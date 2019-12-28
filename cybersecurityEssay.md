
## Flaw 1


A3: Sensitive data exposure, A5: Broken access control.

### Description

The site features a page on /admin/info that lists every bit of information posted on the site, ever. Password - username combinations, names and real addresses are all listed out in plaintext. This might be fine in some niche circumstance, but, the site being accessible without any validation or verification whatsoever only by typing in the somewhat simple url is unacceptable. 

### Steps to replicate

1. start up the server.
2. navigate to localhost:8080. login if you want to. not needed. (one of the default accounts is user: lol, pw: yes)
3. type /admin/info after the 8080 in the url.
4. you should see both all names & address pairs and all username & password pairs in the system listed on the page

### How to fix

First of all, it is debatable whether there should be a page like this in the first place. If we are to keep it in some form, it would be a good idea enforce some kind of permission checking and to remove the most sensitive information from the list. We could see to it that the page only loads if there is a user logged in and / or said user's name is included in a list of approved ones. This could be done in the controller associated with the page. 
___



## Flaw 2


A2: Broken authentication

### Description

Default accounts are preserved in the source code, that is freely available online for anyone to access.

### How to fix

Accounts should be stored in a different manner. E.g. a database could be deployed for the application to use and all data put there. At the very least, the accounts an all other sensitive information should be moved to an environment file from which everything should be retrieved during the application startup.
___



## Flaw 3


A2: Broken authentication, A10: Insufficient logging and monitoring

### Description

The application sports no logging of any kind. This means that it is possible to fuzz a known account's or password's counterpart.


### Steps to replicate

1. As seen with the previous flaw, our app happens to have a fairly badly named account "admin" (you could pick any of these, but this is a propable one to guess.) Pick one of the usernames.
2. Utilise some software that can intercept, modify and send http requests. The OWASP ZAP is all right for this purpose.
3. Use the 10000 most common passwords list found online. Send POST requests to the /login url with parameters username=admin and password=x where x is one of the passwords from the list you retrieved.
4. After you've sent all 10000 requests, sort the responses by headers by size. The one request with the correct password should have the smallest header size, and is the only one that has its location as /done instead of /rekt. 


### How to fix

There isn't any sort of logging in place to prevent fuzzing attacks. This could be solved by, for example, implementing an ApplicationListener that listens for AuthenticationFailureBadCredentialsEvents, and on each detected event (a failed login due to bad credentials, that is) increments a counter keeping track of login failures. A succesful login would reset this counter, but should it surpass the maximum allowed amount of tries, simply blocking the ip address would end most large fuzzing attempts.
___


## Flaw 4


A7: Cross site scripting (XSS)

### Description

On any page that lists out user-inputted content, there are no checks for XSS. Neither is there any input sanitising going on, so the users have free reign on what javascript they want to input and other users to execute when viewing said content.

### Steps to replicate

1. Go on either one of the forms, /form or /login.
2. Input any javascript code you want between script tags, in this manner: <script> document.alert("hacked xd"); </script>. You can add any additional input on either one of the fields, it doensn't particularly matter what you do outside of the script tags.
3. Submit and go on any page that lists your input, for an example our security-averse /admin/info page is fine for this. The code you input loads and executes. 

### How to fix

Spring has built in XSS-prevention. We can deploy this by switching the utext spring-elements used in the html to print out everything to the protected text-elements. Additionally, adding any sort of simple input sanitisation could go a long way in restricting the XSS-opportunities users have. A simple regex check for inputs in the forms or controllers could be a fine start.
___


## Flaw 5


A2: Broken authentication, A3: Sensitive data exposure, A5: Broken access control

### Description


Each account has a user page associated with it. They reside at the path /users/{username}, and currently have no restrictions on who can view whose page. Additionally, users can delete their account by typing their username in the text field and submitting. The deletion controller removes any account with the name the user types regardless of the page it was sent from, so this makes it possible for anyone to somewhat easily delete every other account.

### How to fix

1. The controller for the path /delete already retrieves the current account username, this is just used to print out some vulgarity serverside if the deleted account does not match the one that was signed in at that moment. Gate the deletion action behind said check.
2. Add the same check to the controller that displays the user pages, and redirect somewhere harmless if there is no match.

