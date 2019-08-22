package personal.project.android.firebasenotifications;

import androidx.annotation.NonNull;

public class UserId {
    public String UserId;


    //We pass the id to this class and it simply returns it so that we can use it elsewhere
    public <T extends UserId>T withId(@NonNull final String id) {
        this.UserId = id;
        return (T) this;
    }
}
