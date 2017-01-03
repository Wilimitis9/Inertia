"""
Framework Imports
"""
from django.http import Http404
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, permissions
from oauth2_provider.ext.rest_framework import TokenHasReadWriteScope

"""
Inertia Imports
"""
from InertiaDjango.models import Message
from InertiaDjango.serializers import MessageSerializer
		
"""
Message
"""
class MessageList(APIView):
	permission_classes = [TokenHasReadWriteScope]
	
	# GET: returns the user's Messages
	def get(self, request, format = None):
		messages = request.user.messages.all()
		serializer = MessageSerializer(messages, many = True)
		return Response(serializer.data)
		
	def post(self, request, format = None):
		serializer = MessageSerializer(data = request.data)
		if serializer.is_valid():
			message = serializer.save(user = request.user)
			return Response(serializer.data, status = status.HTTP_201_CREATED)
		return Response(serializer.errors, status = status.HTTP_400_BAD_REQUEST)
