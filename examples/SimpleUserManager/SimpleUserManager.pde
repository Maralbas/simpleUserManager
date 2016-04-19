/* --------------------------------------------------------------------------
 * SimpleUserManager Example for SimpleOpenNI
 * --------------------------------------------------------------------------
 * prog:  Mário Basto
 * date:  17/04/2016 (m/d/y)
 * ----------------------------------------------------------------------------
 * This example shows how the class User Manager can manage active users and
 * users in waiting list
 * ----------------------------------------------------------------------------
 */

import SimpleOpenNI.*;
import simpleUserManager.*;

SimpleOpenNI context;
UserManager  userManager;
float        rotX = radians(180); // the data from openni is upside down

void setup() {
  size(1024, 768, P3D);
  stroke(255);
  context = new SimpleOpenNI(this);
  context.setMirror(true);
  context.enableDepth();

  // setup the User Manager
  // instant change and makes the scanner faster (steps = 60)
  userManager = new UserManager(context, 1, 0, 60); 

  // enable skeleton generation
  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);

  smooth();
  perspective(radians(45), float(width)/float(height), 10, 150000);
}

void draw() {
  context.update(); // update openni

  background(0);
  showUsers();

  translate(width/2, height/2, 0);
  rotateX(rotX);
  scale(0.3);

  // get information about the user for each pixel
  int[] userMap  = context.getUsersPixels(SimpleOpenNI.USERS_ALL);

  userManager.scanUsers(userMap);

  for (int y = 0; y < context.depthHeight (); y += 5) {
    for (int x = 0; x < context.depthWidth (); x += 5) {
      int index = x + y * context.depthWidth();

      for (int i = 0; i < userManager.activeUsers.length; i++) {
        // if the pixel belongs to an active user
        if (userMap[index] == userManager.activeUsers[i][0]) {
          PVector realWorldPoint = context.depthMapRealWorld()[index];
          point(realWorldPoint.x, realWorldPoint.y, realWorldPoint.z);
        }
      }
    }
  }
}

// to see which users are active and which users are in the waiting list
void showUsers() {
  int x = 0;
  int x1 = 0;

  text("ACTIVE USERS :", width - 250, 150);

  for (int i = 0; i < userManager.activeUsers.length; i++) {
    x = 150 + (i + 1) * 20;

    text(userManager.activeUsers[i][0] + ": " + userManager.activeUsers[i][1], 
    width - 250, x);
  }

  text("WAITING LIST :", width - 250, x + 50);

  for (int i = 0; i < userManager.waitingList.size (); i++) {
    x1 = x + 50 + (i + 1) * 20;

    text(userManager.waitingList.get(i)[0] + ": " + userManager.waitingList.get(i)[1], 
    width - 250, x1);
  }
}


// The SimpleOpenNI functions

public void onNewUser(int userId) {
  System.out.println("New user was detected: " + userId);

  userManager.waitingList.add(new Integer[] { // add the new user to the waiting list
    userId, 0
  }
  );
}

public void onLostUser(int userId) {
  System.out.println("Lost user: " + userId);

  userManager.lostUser(userId);
}

