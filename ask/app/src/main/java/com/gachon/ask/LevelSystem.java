package com.gachon.ask;
import androidx.appcompat.app.AppCompatActivity;
import com.gachon.ask.util.Firestore;
import com.gachon.ask.util.model.User;

public class LevelSystem extends AppCompatActivity {
    int level, exp;


    public int addExp(User user, int ex){
        exp = user.getUserExp();
        level = user.getUserLevel();
        exp += ex;
        if(levelUpCheck()) LevelUp(user);
        Firestore.updateUserExp(user.getUid(), exp);
        return exp;
    }

    public void LevelUp(User user){
        level += exp/100;
        exp = exp % 100;
        Firestore.updateUserLevel(user.getUid(), level);
    }

    public boolean levelUpCheck(){
        if(exp >= 100) return true;
        else return false;
    }


}
