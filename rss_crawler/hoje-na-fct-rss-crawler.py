'''
Created on Jun 22, 2014

@author: guilhermemtr
'''

#!/usr/bin/python

# import modules used here -- sys is a very standard one
import sys
import requests
import json
import feedparser
import random
from keepboo_opengraph.opengraph import OpenGraph
from pyplaintext import converter
from bs4 import BeautifulSoup


login_url = "https://greps.herokuapp.com/api/member/login"
news_create_url = "https://greps.herokuapp.com/api/news/create"

content_type = {'Content-Type': 'application/json'}

imgCreatorUrl = ["http://lorempixel.com/1920/1080/abstract"]


VALID_TAGS = ['strong', 'em', 'p', 'ul', 'li', 'br']

def sanitize_html(value):
    soup = BeautifulSoup(value)
    for tag in soup.findAll(True):
        if tag.name not in VALID_TAGS:
            tag.hidden = True
    return soup.renderContents()


def randomImgUrl():
    return random.choice(imgCreatorUrl)

session = None

def initSession():
    return requests.session()

login_data = {
    "email" : None,
    "password" : None,
    "username" : None,
    "imgPath" : None
}

def getCredentials(email, password):
    credentials = login_data.copy()
    credentials["email"] = email
    credentials["password"] = password
    return json.dumps(credentials)

def login(session, email, password):
    print("" + email, password)
    credentials = getCredentials(email, password)
    return session.post(url=login_url, data=credentials, headers=content_type)


news_skeleton = {
    "creationDate": 0,
    "show": False,
    "id": 0,
    "title": None,
    "brief": None,
    "imgPath": None,
    "content": None,
    "tags": [
             {
                "imgPath": None,
                "brief": "",
                "authenticated": False,
                "parents": [
                ],
                "name": "",
                "id": 3
                }
    ],
    "authorId": 0,
    "likes": 0,
    "reports": 0,
    "likeWeight": 0,
    "reportWeight": 0,
    "eventDate": None
}

def getArticleJson(title, content, imgPath):
    news = news_skeleton.copy()
    news["title"] = sanitize_html(title)
    news["brief"] = None
    news["imgPath"] = imgPath
    news["content"] = sanitize_html(content)
    newsJson = json.dumps(news)
    return newsJson

def getArticle(article):
    title = article['title'][:90]
    content = article['description']
    imgPath = randomImgUrl()
    return getArticleJson(title, content, imgPath)

def sendArticle(session, article):
    jsonArticle = getArticle(article)
    return session.post(url=news_create_url, data=jsonArticle, headers=content_type, cookies=session.cookies)


# Gather our code in a main() function
def main():
    # Gets the email, password and rss feed url
    email = sys.argv[1]
    password = sys.argv[2]
    rss_url = sys.argv[3]
    print ('Email ', email)
    print ('Password ', password)
    print ('Rss Feed ', rss_url)
    
    # initializes the feeder session on the campus rest api
    session = initSession()
    print("Initialized session")
    login_response = login(session, email, password)
    print("Logged in")
    print (login_response.text)
    
    print (session.cookies)
    
    # Initializes the feed parser, given the rss url
    feed = feedparser.parse(rss_url)

    counter = 0
    while(counter < len(feed['entries']) and counter < 10):
        news = feed['entries'][counter]
        newsJson = sendArticle(session, news)
        print(newsJson.text)
        counter = counter + 1



# Standard boilerplate to call the main() function to begin
# the program.
if __name__ == '__main__':
    main()
