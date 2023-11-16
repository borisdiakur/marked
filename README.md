# marked

[Confluence](https://www.atlassian.com/software/confluence) macro plugin which renders __remote__ Markdown.

![screenshot showing usage of marked](https://cloud.githubusercontent.com/assets/527049/8500041/d1110e28-2198-11e5-8da2-157c8ac341c3.png) 

## Installation

### Via [Atlassian Marketplace](https://marketplace.atlassian.com/plugins/com.borisdiakur.marked)

1. Log into your Confluence instance as an admin.
2. Click the admin dropdown and choose __Add-ons__. The _Manage add-ons_ screen loads.
3. Click __Find new add-ons__ from the left-hand side of the page.
4. Locate __marked__ via search. Results include add-on versions compatible with your Confluence instance.
5. Click __Install__ to download and install your add-on.
6. You're all set! Click __Close__ in the _Installed and ready to go_ dialog.

### Manually

1. Download the _marked_ jar file either from the [Atlassian Marketplace](https://marketplace.atlassian.com/plugins/com.borisdiakur.marked)
or from [GitHub](https://github.com/borisdiakur/marked). 
2. Log into your Confluence instance as an admin.
3. Click the admin dropdown and choose __Add-ons__. The _Manage add-ons_ screen loads.
4. Click __Upload add-on__ at the top right of the page. The _Upload add-on_ dialog loads.
5. Choose the file from your file system or enter the URL to the location of the raw jar file and click __upload__. And that's it!

## Usage

1. Select _marked_ in the _Select macro_ dialog.
2. Insert the URL of your raw markdown resource in the input field labeled with _URL_.
3. Preview the rendered result by clicking on _Preview_.
4. Insert the rendered content by clicking on _Insert_. You can now preview and save the document.

## FAQ

### 1. Can _marked_ access resources which reside in a private repository?

When working with repositories which require authentication you'll need to use the associated API
in order to access those files.
For example you'll __not__ be able to access a file on a private [__GitLab__](https://about.gitlab.com/) instance
using the following URL:

```
https://gitlab.yourdomain.com/your-group/your-project/blob/master/README.md
```

Instead you will have to authenticate via the [GitLab API](http://doc.gitlab.com/ce/api/README.html).
You might want to add a guest user to your GitLab project and use his/her private token.

In order to get the correct URL you would do the following:

1. Get the project id for a given project name: `https://gitlab.yourdomain.com/api/v3/projects/your-group%2Fyour-project?private_token=your-private-token`
2. Get a list of files for a given project id: `https://gitlab.yourdomain.com/api/v3/projects/your-project-id/repository/tree?private_token=your-private-token`
3. Get the raw file content for a given file id: `https://gitlab.yourdomain.com/api/v3/projects/your-project-id/repository/raw_blobs/your-file-id?private_token=your-private-token`

For private [Bitbucket](https://bitbucket.org/) repositories, token authentication is supported:

1. For the user authorized to access the repository, add a Personal Access Token in the account settings
2. Place the token in the URL: `https://x-token-auth:TOKEN@bitbucket.example.com:PORT/rest/api/1.0/projects/PROJECT/repos/REPO/raw/README.md`

__Note__: When working with another repository management system
you will have to comply with the API given.

_marked_ also supports basic auth (`http[s]://user:password@...`) and bearer (token) auth (`http[s]://x-token-auth:token@...`).

### 2. I get a PKIX path building failed error. What's that? 

Instead of the expected output you might see the following error message:

> Cannot read resource.
  sun.security.validator.ValidatorException: 
  PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: 
  unable to find valid certification path to requested target

The cause of the exception is that the resource host is running over SSL
and your Confluence instance doesn't trust the certificate of that host.   
The solution is to add the resource host's SSL Certificate to the Confluence Java Keystore.
For more information please refer to the [Confluence documentation](https://confluence.atlassian.com/display/DOC/Connecting+to+LDAP+or+JIRA+or+Other+Services+via+SSL).

### 3. Is it free of charge?

Yes.

## Support

If you have any trouble with _marked_ help yourself by [__filing an issue__](https://github.com/borisdiakur/marked/issues)
or even better support back with a [__pull request__](https://github.com/borisdiakur/marked/pulls).

## Contributing

The default [Apache Maven](https://maven.apache.org/) instance boundled with the Atlassian SDK is outdated. If you want to build the plugin yourself, you will need to [change it](https://developer.atlassian.com/server/framework/atlassian-sdk/changing-the-default-maven-version/). The latest version of _marked_ is built with Apache Maven 3.9.5.
Background: Confluence 8.0 has added support for Java 17. To develop apps running against Java 17, you must be running with AMPS 8.3.3 or later. Apache Maven 3.6.3 is the minimum version required starting from AMPS 8.3.0.

## Privacy Notice

_marked_ does not collect any data.

## Credits

_marked_ uses the Markdown processing library [__flexmark-java__](https://github.com/vsch/flexmark-java) under the hood.
