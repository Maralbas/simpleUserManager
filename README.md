# SimpleUserManager for SimpleOpenNI, Processing

This library was created in order to allow for the system to work only with a predefined number of active users, keeping other users in an "waiting list". Everytime one previous active user leaves, it will register as "active" the next user in the "waiting list", and so on.

## Variables

### int[][] activeUsers

Array containing the users that are interacting with the system at the moment. Each user is an array, in the first index is the user's id and in the second index the number 0 (if the system couldn't find the user) or 1(if the user was found).

### ArrayList<Integer[]> waitingList

ArrayList with all the users that are in the queue, waiting to interact with the system. Each user is an array like the activeUsers

### int timer

Variable that will countain the time (in frames) to wait until it changes a user from the waiting list to the activeUsers

### int steps

To scan all users, the class must analyse each pixel. This variable will decide how fast the scan will be. More steps allows for better performance but has the risk of not finding some users.

## Constructors

### UserManager(SimpleOpenNI context, int size, int timer)

### UserManager(SimpleOpenNI context, int size, int timer, int steps)

The variable size define the number of users that will be active (activeUsers)

## Methods

### void addDepthRule(int minDepth, int maxDepth)

To make only users active if they are inside the minDepth and maxDepth

### void scanUsers(int[] userMap)

Will scan the pixels to find which users are on and which are off. The array userMap you can get from ```context.getUsersPixels(SimpleOpenNI.USERS_ALL)```

### void scanUsers(int[] userMap, int[] depthMap)

The depthMap is necessary to compare with minDepth and maxDepth

### void lostUser(int id)

This method must be called inside the function of the SimpleOpenNI onLostUser(int userId)
