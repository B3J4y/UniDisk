from firebase_admin import auth,initialize_app
import os

email = os.environ["unidisk_email"]
password = os.environ["unidisk_password"]


initialize_app()
user = auth.create_user(
    email=email,
    password=password,
    disabled=False)
print('Sucessfully created new user: {0}'.format(user.uid))
