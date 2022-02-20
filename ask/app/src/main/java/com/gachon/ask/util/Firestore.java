package com.gachon.ask.util;

import com.gachon.ask.util.model.Stock;
import com.gachon.ask.util.model.StockReport;
import com.gachon.ask.util.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;

public class Firestore {

    /**
     * Firestore의 Instance를 반환한다
     * @author Taehyun Park
     * @return FirebaseFirestore Instance
     */
    public static FirebaseFirestore getFirestoreInstance() {
        return FirebaseFirestore.getInstance();
    }

    /**
     * 고유 userId값으로 유저 객체를 얻어온다
     * @author Taehyun Park
     * @param userId
     * @return Task<DocumentSnapshot>
     */
    public static Task<DocumentSnapshot> getUserData(String userId){
        return getFirestoreInstance().collection("user").document(userId).get();
    }

    /**
     * 고유 userId값으로 유저의 자산을 update
     * @author Taehyun Park
     * @param userId
     * @param userMoney
     * @return Task<Void>
     */
    public static Task<Void> updateUserMoney(String userId, Integer userMoney){
        return getFirestoreInstance().collection("user").document(userId).update("userMoney",userMoney);
    }

    /**
     * 고유 userId값으로 유저의 보유 주식을 update
     * @author Taehyun Park
     * @param userId
     * @param userStock
     * @return Task<Void>
     */
    public static Task<Void> updateUserStock(String userId, ArrayList<Stock> userStock){
        return getFirestoreInstance().collection("user").document(userId).update("myStock",userStock);
    }

    /**
     * 고유 userId값으로 유저의 level update
     * @author Minha Roh
     * @param userId
     * @param userLevel
     * @return Task<Void>
     */
    public static Task<Void> updateUserLevel(String userId, Integer userLevel){
        return getFirestoreInstance().collection("user").document(userId).update("userLevel",userLevel);
    }

    /**
     * 고유 userId값으로 유저의 exp update
     * @author Minha Roh
     * @param userId
     * @param userExp
     * @return Task<Void>
     */
    public static Task<Void> updateUserExp(String userId, Integer userExp){
        return getFirestoreInstance().collection("user").document(userId).update("userExp",userExp);
    }

    /**
     * 자신의 프로필에서 닉네임을 update
     * @author Taehyun Park
     * @param userId
     * @param nickName
     * @return Task<Void>
     */
    public static Task<Void> updateProfileNickName(String userId, String nickName){
        return getFirestoreInstance().collection("user").document(userId).update("userNickName",nickName);
    }


    /**
     * 새로운 유저의 정보를 DB에 추가하도록 요청한다
     * @author Taehyun Park
     * @param userId
     * @param userEmail
     * @param userNickName
     * @param userLevel
     * @param userExp
     * @param userMoney
     * @param userRank
     * @param postAnalysisNum
     * @param postQuestionNum
     * @param postAnswerNum
     * @param profitRate
     * @param myStock
     * @param myStockReport
     * @param challenges
     * @return Task<Void>
     */
    public static Task<Void> writeNewUser(String userId, String userEmail, String userNickName, int userLevel, int userExp, int userMoney, int userRank, int postAnalysisNum, int postQuestionNum,
                                          int postAnswerNum, float profitRate, ArrayList<Stock> myStock, ArrayList<StockReport> myStockReport, ArrayList<Integer> challenges) {
        User newUser = new User(userId, userEmail, userNickName, new Timestamp(new Date()), userLevel, userExp, userMoney, userRank, postAnalysisNum, postQuestionNum, postAnswerNum, profitRate, myStock, myStockReport, challenges);
        return getFirestoreInstance().collection("user").document(userId).set(newUser);
    }

}
