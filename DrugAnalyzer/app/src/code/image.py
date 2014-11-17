__author__ = 'abarbi'

from pytesser import*
image=Image.open('tab1.jpg')
text=image_to_string(image)
print text