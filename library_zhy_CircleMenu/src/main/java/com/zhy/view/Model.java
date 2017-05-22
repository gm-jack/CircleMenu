package com.zhy.view;

import java.util.List;

/**
 * Created by admin on 2017/5/22.
 */

public class Model {


    /**
     * result : [{"during":"10","icon":"/ringhelper/resources/images/secret/iconDriving.png","id":1,"name":"开车","sceneRingCodes":"33f23c89-750a-4311-b537-e786e73c6a82,b0d328b5-4f7c-4220-84e6-742479d6c7a1","type":"0","userId":"0"},{"during":"10","icon":"/ringhelper/resources/images/secret/iconMeeting.png","id":2,"name":"会议","sceneRingCodes":"","type":"0","userId":"0"},{"during":"10","icon":"/ringhelper/resources/images/secret/iconRest.png","id":3,"name":"休息","sceneRingCodes":"","type":"0","userId":"0"},{"during":"10","icon":"/ringhelper/resources/images/secret/iconEating.png","id":4,"name":"吃饭中","sceneRingCodes":"","type":"0","userId":"0"},{"during":"10","icon":"/ringhelper/resources/images/secret/iconExercise.png","id":5,"name":"运动中","sceneRingCodes":"","type":"0","userId":"0"},{"during":"10","icon":"/ringhelper/resources/images/secret/iconMovie.png","id":6,"name":"电影中","type":"0","userId":"0"},{"during":"1434","icon":"","id":20,"name":"接口","sceneRingCodes":"6","type":"1","userId":"39"}]
     * resultCode : 1
     * resultMsg : 数据获取成功
     */

    private String resultCode;
    private String resultMsg;
    private List<ResultBean> result;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * during : 10
         * icon : /ringhelper/resources/images/secret/iconDriving.png
         * id : 1
         * name : 开车
         * sceneRingCodes : 33f23c89-750a-4311-b537-e786e73c6a82,b0d328b5-4f7c-4220-84e6-742479d6c7a1
         * type : 0
         * userId : 0
         */

        private String during;
        private String icon;
        private int id;
        private String name;
        private String sceneRingCodes;
        private String type;
        private String userId;

        public String getDuring() {
            return during;
        }

        public void setDuring(String during) {
            this.during = during;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSceneRingCodes() {
            return sceneRingCodes;
        }

        public void setSceneRingCodes(String sceneRingCodes) {
            this.sceneRingCodes = sceneRingCodes;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
