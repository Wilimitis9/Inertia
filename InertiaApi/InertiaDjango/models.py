from __future__ import unicode_literals
import uuid
from django.contrib.auth.models import User
from django.db import models

# Create your models here.
class AbstractEntity(models.Model):
	created = models.DateTimeField(auto_now_add=True)
	updated = models.DateTimeField(auto_now=True)
	is_active = models.BooleanField(default=True)
	
	class Meta:
		abstract = True
		
class Message(AbstractEntity):
	text = models.CharField(max_length=50)
	user = models.ForeignKey(User, related_name = 'messages', on_delete=models.CASCADE)
	uuid = models.UUIDField(default = uuid.uuid4, unique = True)
