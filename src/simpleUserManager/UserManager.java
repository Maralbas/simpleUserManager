/* --------------------------------------------------------------------------
 * SimpleUserManager for SimpleOpenNI
 * --------------------------------------------------------------------------
 * prog:  Mário Basto
 * date:  17/04/2016 (m/d/y)
 * ----------------------------------------------------------------------------
 * This library was made ​​to help the system work only with the predefined number
 * of users, keeping others in a waiting list allowing their participation if
 * any user who was interacting with the system would leave
 * ----------------------------------------------------------------------------
 */


package simpleUserManager;

import SimpleOpenNI.*;
import java.util.ArrayList;

public class UserManager {
  private SimpleOpenNI context;
  private int time[]; // how much time each active user is off

  // list of the id of the active users
  public int[][] activeUsers;

  // users that are in the waitingList
  public ArrayList<Integer[]> waitingList = new ArrayList<Integer[]>();

  public int timer; // how much time (in frames) it will change a active user to a user in the waiting list
  public int steps; // more steps gives better performance but has the risk of not find some users

  // configuration for the constructor
  private void configure(SimpleOpenNI context, int size, int timer) {
    this.activeUsers = new int[size][2];
    this.time = new int[size];

    for (int i = 0; i < activeUsers.length; i++) {
      this.activeUsers[i][0] = -1; // -1 means that there are no user in that position
      this.activeUsers[i][1] = 0; // 0 is off, 1 is on

      this.time[i] = 0;
    }

    this.timer   = timer;
    this.context = context;
  }

  public UserManager(SimpleOpenNI context, int size, int timer) {
    this.configure(context, size, timer);
    this.steps = 20;
  }

  public UserManager(SimpleOpenNI context, int size, int timer, int steps) {
    this.configure(context, size, timer);
    this.steps = steps;
  }

  // turn all users off
  public void resetUsers() {
    for (int i = 0; i < activeUsers.length; i++) {
      activeUsers[i][1] = 0;
    }

    for (int i = 0; i < waitingList.size (); i++) {
      waitingList.get(i)[1] = 0;
    }
  }

  // scan all the users to see who are on and who are off
  public void scanUsers(int[] userMap) {
    resetUsers(); // makes all users off so that the next code discover only the users that are on

    for (int y = 0; y < context.depthHeight (); y += steps) {
      for (int x = 0; x < context.depthWidth (); x += steps) {
        boolean cont = false; // to not waste time looping unnecessary interactions

        int index = x + y * context.depthWidth();

        // active users
        for (int i = 0; i < activeUsers.length; i++) {
          // if the pixel belongs to an active user
          if (userMap[index] == activeUsers[i][0]) {
            activeUsers[i][1] = 1;

            cont = true;
            break;
          }
        }

        if (cont) continue;

        // waiting list users
        for (int i = 0; i < waitingList.size (); i++) {
          // if the pixel belongs to a user in the waiting list
          if (userMap[index] == waitingList.get(i)[0]) {
            waitingList.get(i)[1] = 1;

            cont = true;
            break;
          }
        }

        if (cont) continue;
      }
    }

    checkActiveUsers();
  }

  // see if there are some active user that is off
  public void checkActiveUsers() {
    for (int i = 0; i < activeUsers.length; i++) {

      if (activeUsers[i][1] == 0) { // if the user is off, increase time
        time[i]++;

        // change the active user if it exceeds the time limit
        if (time[i] >= timer) changeActiveUser(i);
      } else { // if the user is on, reset the time to 0
        time[i] = 0;
      }
    }
  }

  // change user that is on in the waiting list to the position of the active user that now is off
  // also moves the user that was active to the waiting list
  void changeActiveUser(int x) {
    int id = activeUsers[x][0];

    boolean check = false;
    for (int i = 0; i < waitingList.size (); i++) {
      if (waitingList.get(i)[1] == 1) { // if the user is on
        activeUsers[x][0] = waitingList.get(i)[0];

        waitingList.remove(i);

        check = true;
        break;
      }
    }

    // if there are no user on in the waiting list, changes the active user id to -1
    if (!check) activeUsers[x][0] = -1;

    if (id != -1) waitingList.add(new Integer[] {
      id, 0
    }
    ); // move the i user to the waiting list in case he comes back
  }

  // when user is lost remove from the waiting list
  public void lostUser(int id) {
    boolean end = false; // to not look inside active users if the user is already found

    for (int i = 0; i < waitingList.size (); i++) {
      if (waitingList.get(i)[0] == id) {
        waitingList.remove(i);

        end = true;

        break;
      }
    }

    if (!end) { // only look inside active users if the user is not in the waiting list
      for (int i = 0; i < activeUsers.length; i++) {
        if (activeUsers[i][0] == id) {
          activeUsers[i][0] = -1;

          break;
        }
      }
    }
  }


  // ----- SimpleOpenNI Methods -------

  public void onNewUser(int userId) {
    System.out.println("Detected new user: " + userId);

    waitingList.add(new Integer[] { // add the new user to the waiting list
      userId, 0
    }
    );

    context.requestCalibrationSkeleton(userId, true);
  }

  public void onLostUser(int userId) {
    System.out.println("Lost user: " + userId);

    lostUser(userId);
  }

  public void onStartCalibration(int userId) {
    System.out.println("Start calibration for user: " + userId);
  }

  public void onEndCalibration(int userId, boolean successfull) {
    if (successfull) {
      System.out.println("User " + userId + " calibrated");
      context.startTrackingSkeleton(userId);
    } else {
      System.out.println("Couldn't calibrate user " + userId);
    }
  }
}
