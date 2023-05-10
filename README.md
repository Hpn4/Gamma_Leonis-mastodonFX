# Gamma Leonis Mastodon Client

This project is a Client for the [Mastodon](https://joinmastodon.org/) social network.

## 1. Pre-requisites

There is no pre-requisites to run the client.
Config file and database will be created automatically.

Just clone the repo and launch the client. The first time you will have to enter the client key and the client secret of your Mastodon application.
Please enter the real value because we cannot know if the value is correct or not.
In fact since they are secret data, the REST API of mastodon do not offer any way to check if the value is correct or not.

---

## 2. Account switching

To change from one account to another one they are two ways:
1. When you opened the client for the first time.
2. By clicking on the settings button on the main window

In booth cases, you will have a login screen with saved accounts on a left scroll pane.
And a login form on the right.

---

## 3. Implemented use case

Personal:
- You can log in multiple accounts and switch between them.
- You can see your home timeline.
- You can see your bookmarked toots.
- You can see your favourites toots.

Account:
- Follow/unfollow accounts.
- Remove an account from your followers.
- Profile of account (posted toots, followers, following, avatar, etc.)
- By clicking on an account in the followers/following list, you will be redirected to the account of the user.

Search and trending:
- You can search for toots, accounts and hashtags.
- You can see the trending hashtags and toots in the trending section.

Toots action:
- Favourite/unfavourite.
- Bookmark/unbookmark.
- Reply.
- Delete (only if it's your own toot).
- Unboost. **For an inexpliquable reason, the REST API of Mastodon do not respond when boosting a toot.**
- By clicking on a link in a toot, you will be redirected to the web page in your default browser.
- By clicking on a hashtag in a toot, you will be redirected to the timeline of this hashtag.
- By clicking on a mention in a toot, you will be redirected to the account of the user.

Send toot:
- Warning content/spoiler is supported.
- You can mention user, hashtag and link in a toot.
- You can send a toot in PUBLIC (everyone can see it) or DIRECT (only the mentioned users can see it).

## 4. Technical details

- We use Hibernate and JPA to manage the database.
- We use log4j in order to log the errors (saved in `logs/app.log`).
- We use JUnit to test the api.

---


Mockup are located in the folder `docs` and are made with [Draw.io](https://draw.io/).
