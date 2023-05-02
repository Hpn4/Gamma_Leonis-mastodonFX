# Gamma Leonis Mastodon Client

This project is a Rest API for the the platform Mastodon.

## 1. Pre-requisites

### 1.1 Properties file

In order for the client to work, you need to create the `config.propeties` file by renaming the `config_template.properties` file.

You just need to fill the two above lines (**Leave the other lines as they are**):

```properties
app.client.id=Your application client id
app.client.secret=Your application client secret
```

### 1.2 Database

We use hibernate and JPA to manage the database. But you have nothing to do to set it up.
The database and tables will be created automatically.

---

## 2. Account switching

To change from one account to another one they are two ways:
1. When you opened the client for the first time.
2. By clicking on the settings button on the main window

In booth cases, you will have a login screen with saved accounts on a left scroll pane.
And a login form on the right.

---

Mockup are located in the folder `docs` and are made with [Draw.io](https://draw.io/).
