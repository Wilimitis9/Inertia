from rest_framework import serializers
from InertiaDjango.models import Message

class MessageSerializer(serializers.ModelSerializer):
	
	class Meta:
		model = Message
		fields = ('text', 'created', 'uuid')
