# SimpleUserManager for SimpleOpenNI, Processing

This library was made ​​to help the system work only with the predefined number of users, keeping others in a waiting list allowing their participation if any user who was interacting with the system would leave

## Variables

### int[][] activeUsers

Array containing the users that are interacting with the system in the moment. Each user is an array, in the first index the id of the user, and the second index the number 0 if the system couldn't find him or 1 if he is found.

### ArrayList<Integer[]> waitingList

ArrayList with all the users that are in the queue to interact with the system. Each user is an array like the activeUsers

### int timer

Variable that will have the time (in frames) to wait until it changes some user in the waiting list to the activeUsers

### int steps

To scan all users, the class must analyse each pixel. This variable will decide how much faster the scan will be. More steps gives better performance but has the risk of not find some users.

## Constructors

### UserManager(SimpleOpenNI context, int size, int timer)

### UserManager(SimpleOpenNI context, int size, int timer, int steps)

The variable size define the number of users that will be active (activeUsers)

## Methods

## void addDepthRule(int minDepth, int maxDepth)

To make only users active if they are inside the minDepth and maxDepth

### void scanUsers(int[] userMap)

Will scan the pixels to find which users are on and which are off. The array userMap you can get from ```context.getUsersPixels(SimpleOpenNI.USERS_ALL)```

### void scanUsers(int[] userMap, int[] depthMap)

The depthMap is necessary to compare with minDepth and maxDepth

### void lostUser(int id)

This method must be called inside the function of the SimpleOpenNI onLostUser(int userId)
