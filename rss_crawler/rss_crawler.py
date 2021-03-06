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
from keepboo_opengraph.opengraph import OpenGraph
from pyplaintext import converter


login_url = "https://greps.herokuapp.com/api/member/login"
news_create_url = "https://greps.herokuapp.com/api/news/create"

content_type = {'Content-Type': 'application/json'}

session = None

tags = []

def initSession():
    return requests.session()

login_data = {
    "email" : None,
    "password" : None
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


tag_skeleton = {
    "id" : -1,
    "name" : None
}

def getTag(name):
    tag = tag_skeleton.copy()
    tag["name"] = name
    return tag


news_skeleton = {
    "show": False,
    "title": None,
    "brief": None,
    "imgPath": None,
    "content": None,
    "tags": [
             
    ],
    "eventDate": None
}

def getArticleJson(title, content, imgPath):
    news = news_skeleton.copy()
    parser = converter.HTML2PlainParser()
    news["title"] = parser.html_to_plain_text(title)
    parser = converter.HTML2PlainParser()
    news["brief"] = parser.html_to_plain_text(content)[:140]
    news["imgPath"] = imgPath
    parser = converter.HTML2PlainParser()
    news["content"] = parser.html_to_plain_text(content)
    news["tags"] = tags
    newsJson = json.dumps(news)
    return newsJson

def getArticle(article):
    print(article)
    og = OpenGraph(url=article['link'])
    print(og)
    title = article['title'][:90]
    content = article['summary']
    imgPath = og['image']
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

    
    i = 0
    for tag in sys.argv[4:] :
        print('Tag ', tag)
        tags.insert(i,getTag(tag))
        i = i + 1



    
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
    while(counter < len(feed['entries'])):
        try:
            print('Getting entry ', counter)
            news = feed['entries'][counter]
            newsJson = sendArticle(session, news)
            print(newsJson.text)
        except:
            pass
        counter = counter + 1



# Standard boilerplate to call the main() function to begin
# the program.
if __name__ == '__main__':
    main()
