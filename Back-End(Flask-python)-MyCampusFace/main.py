from flask import Flask
import cv2
import numpy as np
import json
import os.path
import werkzeug
from PIL import Image
from numpy import asarray
import pandas as pd
import csv
 
# import request
from flask import request,Response
from flask_sqlalchemy import SQLAlchemy
import sqlite3
from datetime import date


#function to detect face from image
def faceDetect(img,filename):
    try:
        face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray, 1.3, 5)
        for (x,y,w,h) in faces:
            img = cv2.rectangle(img,(x,y),(x+w,y+h),(255,0,0),2)
        #save file
        path_file = ("static/User."+filename)
        cv2.imwrite(path_file, gray[y:y+h,x:x+w])
        #response
        resp = "Image Uploaded Successfully"
    except:
        resp = "This image does not have a face"
        print(resp)
    #response
    return resp 


#function to get images and ids
def getImagesAndLabels(path):
	imagePaths = [os.path.join(path, f) for f in os.listdir(path)]
	faces = []
	Ids = []
	for imagePath in imagePaths:
		pilImage = Image.open(imagePath).convert('L')
		imageNp = np.array(pilImage, 'uint8')
		Id = int(os.path.split(imagePath)[-1].split(".")[1])
		faces.append(imageNp)
		Ids.append(Id)
	return np.array(Ids),faces


#function for train images
def TrainImages():
    resp = "This image does not have a face, so cannot train image"
    try:
        #recognizer=cv2.face.createEigenFaceRecognizer()
        #recognizer=cv2.face.createFisherFaceRecognizer()
        recognizer=cv2.face.LBPHFaceRecognizer_create()
        path="static"
        Ids,faces= getImagesAndLabels(path)
        recognizer.train(faces,Ids)
        recognizer.save('trainingData.yml')
    except:
        print(resp)
        
 
        
        
#API   
app = Flask(__name__)
 
@app.route("/")
def showHomePage():
    return "home page"


connect = sqlite3.connect('mycampusface.db')
connect.execute(
    'CREATE TABLE IF NOT EXISTS ETUDIANT ( id int(255) NOT NULL, username varchar(50) NOT NULL,\
    usersurname varchar(50) NOT NULL,usermatricule varchar(7) NOT NULL, userfiliere varchar(50) NOT NULL, userniveau varchar(5) NOT NULL,usertransaction varchar(20) DEFAULT NULL,usertranche varchar(5) DEFAULT NULL,userprice varchar(20) DEFAULT NULL)')

conn = sqlite3.connect('mycampusface.db')
conn.execute(
    'CREATE TABLE IF NOT EXISTS RECONNU ( id int(255) NOT NULL, username varchar(50) NOT NULL,\
    usersurname varchar(50) NOT NULL,usermatricule varchar(7) NOT NULL, userfiliere varchar(50) NOT NULL, userniveau varchar(5) NOT NULL,usertransaction varchar(20) DEFAULT NULL,usertranche varchar(5) DEFAULT NULL,userprice varchar(20) DEFAULT NULL,annee date NOT NULL)')



@app.route("/getuserid")
def getUserId():
    connect = sqlite3.connect('mycampusface.db')
    cursor = connect.cursor()
    cursor.execute('SELECT * FROM ETUDIANT')

    data = cursor.fetchall()
               
    if len(data)==0:
        return "1"
    else:
        num = len(data)+1
        return str(num) 
    
 
@app.route("/sample", methods=["POST"])
def debug():
    text = request.form["sample"]
    text = text.split('-')
    with sqlite3.connect('mycampusface.db') as users:
        cursor = users.cursor()
        cursor.execute("INSERT INTO ETUDIANT(id,username,usersurname,usermatricule,userfiliere,userniveau,usertransaction,usertranche,userprice) VALUES (?,?,?,?,?,?,?,?,?)",
                       (text[0],text[1],text[2],text[3],text[4],text[5],text[6],text[7],text[8]))
    users.commit()
    print(text)
    return "received" 


@app.route("/api/upload", methods=["GET","POST"])
def upload():
    #retrieve image from client 
    imagefile = request.files["image"]
    filename = werkzeug.utils.secure_filename(imagefile.filename)
    print("\nReceived image File name : " + imagefile.filename)
    imagefile.save("ClientImages/"+filename)
    img = asarray(Image.open("ClientImages/"+filename))
    #process image
    img_processed = faceDetect(img,filename)
    TrainImages()
    #Response
    return img_processed


@app.route("/api/recognize_faces", methods=["GET","POST"])
def detect_faces():   
    resp = "Unknown"    
    #receive image of faces
    our_image = request.files["image"]
    filename = werkzeug.utils.secure_filename(our_image.filename)
    #save that image to directory and transform that image to numpy array
    print("\nReceived image File name : " + our_image.filename)
    our_image.save("imagesTorecognize/"+filename)
    image = asarray(Image.open("imagesTorecognize/"+filename))
    
    #Start recognition

    try:
        #rec=cv2.face.createEigenFaceRecognizer()
        #rec=cv2.face.createFisherFaceRecognizer()
        rec=cv2.face.LBPHFaceRecognizer_create()
        rec.read("trainingData.yml")
        face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        faces = face_cascade.detectMultiScale(gray, 1.1, 4)
        
        for (x, y, w, h) in faces:
            # To draw a rectangle in a face
            cv2.rectangle(image, (x, y), (x+w, y+h), (255, 255, 0), 2)
            id, uncertainty = rec.predict(gray[y:y+h, x:x+w])
            print(id, uncertainty)
            
            connect = sqlite3.connect('mycampusface.db')
            cursors = connect.cursor()
            cursors.execute('SELECT * FROM ETUDIANT WHERE id={}'.format(id))
            datas = cursors.fetchall()
        
            if (uncertainty<= 120):
                for row in datas:
                    resp = str(row[0])+','+str(row[1])+','+str(row[2])+','+str(row[3])+','+str(row[4])+','+str(row[5])+','+str(row[6])+','+str(row[7])+','+str(row[8])
                    with sqlite3.connect('mycampusface.db') as user:
                        cursors = user.cursor()
                        cursors.execute("INSERT INTO RECONNU(id,username,usersurname,usermatricule,userfiliere,userniveau,usertransaction,usertranche,userprice,annee) VALUES (?,?,?,?,?,?,?,?,?,?)",(str(row[0]),str(row[1]),str(row[2]),str(row[3]),str(row[4]),str(row[5]),str(row[6]),str(row[7]),str(row[8]),date.today()))
                    user.commit()
            else:
                resp = "Unknown"     
    except:
        resp
    print (resp) 
    #Response
    return resp


@app.route("/api/users_recognize")
def getUsers():
    resp = "Table is empty!"
    try:
        con = sqlite3.connect('mycampusface.db')
        cursor = con.cursor()
        cursor.execute('SELECT DISTINCT id,username,usersurname,usermatricule,userfiliere,userniveau,usertransaction,usertranche,userprice,annee FROM RECONNU')

        data = cursor.fetchall()
        for row in data:
            resp = str(row[0])+','+str(row[1])+','+str(row[2])+','+str(row[3])+','+str(row[4])+','+str(row[5])+','+str(row[6])+','+str(row[7])+','+str(row[8])+','+str(row[9])
            print(resp) 
    except:
       resp = "Table is empty!"
       
    print(resp)    
    return resp
                       


if __name__ == "__main__":
  app.run(host="0.0.0.0",port=5000,debug=True)