/* main interface for the application
 *
 * handle data passing between main activity
 * and event listeners
 *
 */
package com.incidentlocator.client;

import android.location.Location;

public interface IncidentLocatorInterface {
    public void updateLocation(Location loc);
}
