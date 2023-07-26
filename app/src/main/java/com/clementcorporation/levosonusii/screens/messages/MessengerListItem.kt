package com.clementcorporation.levosonusii.screens.messages

data class MessengerListItem(val threadId: String, val message: String, val date: String, val time: String,
                             val userIconUrl: String = "", val user1: String, val user2: String)