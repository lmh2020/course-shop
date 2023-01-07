<template>
  <div class="app-container" style="background-color: #eee;min-height: calc(100vh - 84px);">

    <div id="myExam">
      <div class="wrapper">
        <ul class="top">
          <li class="order" @click="checkedState('')">
            <div :class="{active: !queryParams.state}" class="item">全部</div>
          </li>
          <li class="order" @click="checkedState('AWAITING')">
            <div :class="{active: queryParams.state === 'AWAITING'}" class="item">未开始</div>
          </li>
          <li class="order" @click="checkedState('UNDERWAY')">
            <div :class="{active: queryParams.state === 'UNDERWAY'}" class="item">已开始</div>
          </li>
          <li class="order" @click="checkedState('FINISHED')">
            <div :class="{active: queryParams.state === 'FINISHED'}" class="item">已结束</div>
          </li>
          <li class="search-li"><div class="icon"><input type="text" v-model="queryParams.examTitle" placeholder="试卷名称" class="search"><i class="el-icon-search"></i></div></li>
          <li><el-button type="primary" @click="getList()">搜索试卷</el-button></li>
        </ul>
        <el-row>
          <el-col :span="9" :offset="2" v-for="(exam, index) in examList" :key="index" style="margin-top: 20px;">
            <div>
              <div class="profile-card js-profile-card">
                <div class="profile-card__img">
                  <img :src="exam.coverImg" alt="profile card">
                </div>
                <div class="profile-card__cnt js-profile-cnt">
                  <div class="profile-card__name" v-text="exam.title"></div>
                  <div class="profile-card__txt" v-text="exam.remark"></div>
                  <div class="profile-card-inf">
                    <div class="profile-card-inf__item">
                      <div class="profile-card-inf__title" v-text="exam.totalScore"></div>
                      <div class="profile-card-inf__txt">总分</div>
                    </div>
                    <div v-if="exam.examineRecord.examRecordState.code === 'FINISHED'" class="profile-card-inf__item">
                      <div class="profile-card-inf__title" v-text="exam.examineRecord.score"></div>
                      <div class="profile-card-inf__txt">得分</div>
                    </div>
                    <div class="profile-card-inf__item">
                      <div class="profile-card-inf__title" v-text="exam.difficultyLevel"></div>
                      <div class="profile-card-inf__txt">难度</div>
                    </div>
                    <div class="profile-card-inf__item">
                      <div class="profile-card-inf__title" v-text="exam.examineRecord.examRecordState.desc"></div>
                      <div class="profile-card-inf__txt">状态</div>
                    </div>
                  </div>
                  <div class="profile-card-ctr">
                    <a v-if="exam.examineRecord.examRecordState.code == 'AWAITING' || exam.examineRecord.examRecordState.code == 'UNDERWAY'" @click="selectedExam = exam"
                       href="#popup-article" class="profile-card__button button--blue js-message-btn">去考试</a>
                    <button v-if="exam.examineRecord.examRecordState.code == 'AWAITING'" @click="toRevokeExam(exam)" class="profile-card__button button--orange">撤销</button>
                    <button v-else-if="exam.examineRecord.examRecordState.code == 'FINISHED'" @click="toExamineResultPage(exam)" class="profile-card__button button--green">查看详情</button>
                  </div>
                </div>
              </div>
            </div>
          </el-col>

        </el-row>
      </div>
    </div>

    <div v-if="selectedExam" id="popup-article" class="popup">
      <div class="popup__block">
        <div class="wrapper">
          <examAttention :exam="selectedExam"></examAttention>
        </div>
        <a @click="selectedExam = null" href="#" class="popup__close">关闭</a>
      </div>
    </div>

  </div>

</template>

<script>

import {listExamRecord, revokeExam} from "@/api/edu/examRecord";
import examAttention from "@/views/components/attention/examAttention";

export default {
  name: "examList",
  components: {examAttention},
  data() {
    return {
      examStartTimeRange: [],
      // 查询参数
      queryParams: {
        examTitle: null,
        state: null,
        examTimeBegin: null,
        examTimeEnd: null,
      },
      selectedExam: null,
      loading: false,
      examList: [], //所有考试信息
    };
  },
  created() {
    this.getList();
  },
  methods: {
    checkedState(state) {
      this.queryParams.state = state
      this.getList()
    },

    /** 查询考核列表 */
    getList() {
      this.loading = true;
      this.examList = [];
      listExamRecord(this.addDateRange(this.queryParams, this.examStartTimeRange)).then(response => {
        this.examList = response.data || [];
        this.loading = false;
      });
    },

    /* 撤销已报名的考试 */
    toRevokeExam(exam){
      this.$confirm(`确定撤销考核[${exam.title}]的报名考试吗？`, "警告", {
        confirmButtonText: '撤销',
        cancelButtonText: '关闭',
        type: "warning"
      }).then(function() {
        return revokeExam(exam.examineRecord.recordCollectId)
      }).then(() => {
        this.getList();
        this.msgSuccess("撤销成功");
      })
    },

    toExamineResultPage(exam){
      this.$router.push({path: `/examine/result/${exam.examineRecord.recordCollectId}`})
    },

  }
};
</script>

<style lang="scss" scoped>
  .wrapper .top .order {
    cursor: pointer;
  }
  .wrapper .top .order:hover {
    color: #0195ff;
    //border-bottom: 2px solid #0195ff;
  }
  .wrapper .top .order .active {
    color: #0195ff;
    border-bottom: 3px solid #0195ff;
  }
  .wrapper li .el-switch__label {
    font-size: 18px;
  }
  .item .info i {
    margin-right: 5px;
    color: #0195ff;
  }
  .item .info span {
    margin-right: 14px;
  }
  .top .el-icon-search {
    position: absolute;
    right: 10px;
    top: 10px;
  }
  .top .icon {
    position: relative;
  }
  .wrapper .top {
    border-bottom: 1px solid #eee;
    margin-bottom: 20px;
    line-height: 25px;
    font-size: 18px;
  }
  #myExam .search-li {
    margin-left: auto;
  }
  .top .search-li {
    margin-left: auto;
  }
  .top li {
    display: flex;
    align-items: center;
  }
  .top .search {
    margin-left: auto;
    padding: 10px;
    border-radius: 4px;
    border: 1px solid #eee;
    box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
    transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
  }
  .top .search:hover {
    color: #0195ff;
    border-color: #0195ff;
  }
  .wrapper .top {
    display: flex;
  }
  .wrapper .top li {
    margin: 20px;
  }
  #myExam {
    width: 980px;
    margin: 0 auto;
  }
  #myExam .title {
    margin: 20px;
  }
  #myExam .wrapper {
    background-color: #fff;
  }
  .item .info i {
    margin-right: 5px;
    color: #0195ff;
  }
  .item .info span {
    margin-right: 14px;
  }
  .top .el-icon-search {
    position: absolute;
    right: 10px;
    top: 10px;
  }
  .top .icon {
    position: relative;
  }
  .wrapper .top {
    border-bottom: 1px solid #eee;
  }
  #myExam .search-li {
    margin-left: auto;
  }
  .top .search-li {
    margin-left: auto;
  }
  .top li {
    display: flex;
    align-items: center;
  }
  .top .search {
    margin-left: auto;
    padding: 10px;
    border-radius: 4px;
    border: 1px solid #eee;
    box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
    transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
  }
  .top .search:hover {
    color: #0195ff;
    border-color: #0195ff;
  }
  .wrapper .top {
    display: flex;
  }
  .wrapper .top li {
    margin: 20px;
  }
  #myExam {
    width: 90%;
    margin: 0 auto;
  }
  #myExam .title {
    margin: 20px;
  }
  #myExam .wrapper {
    background-color: #fff;
    min-height: calc(70vh);
  }


  /* 考核卡片 */
  .profile-card {
    //width: 90%;
    min-height: 420px;
    margin-top: 75px;
    box-shadow: 0px 8px 60px -10px rgba(13, 28, 39, 0.6);
    background: #fff;
    border-radius: 12px;
    //max-width: 700px;
    position: relative;
  }
  .profile-card.active .profile-card__cnt {
    filter: blur(6px);
  }
  .profile-card__img {
    width: 150px;
    height: 150px;
    margin-left: auto;
    margin-right: auto;
    transform: translateY(-50%);
    border-radius: 50%;
    overflow: hidden;
    position: relative;
    //z-index: 4;
    box-shadow: 0px 5px 50px 0px #6c44fc, 0px 0px 0px 7px rgba(107, 74, 255, 0.5);
  }
  @media screen and (max-width: 576px) {
    .profile-card__img {
      width: 120px;
      height: 120px;
    }
  }
  .profile-card__img img {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
  }
  .profile-card__cnt {
    margin-top: -35px;
    text-align: center;
    //padding: 0 20px;
    padding-bottom: 5px;
    transition: all .3s;
  }
  .profile-card__name {
    font-weight: 700;
    font-size: 24px;
    color: #6944ff;
    margin-bottom: 15px;
  }
  .profile-card__txt {
    font-size: 18px;
    font-weight: 500;
    color: #324e63;
    margin-bottom: 15px;
  }
  .profile-card__txt strong {
    font-weight: 700;
  }
  .profile-card-inf {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    align-items: flex-start;
    //margin-top: 35px;
  }
  .profile-card-inf__item {
    padding: 10px 15px;
    min-width: 100px;
  }
  @media screen and (max-width: 768px) {
    .profile-card-inf__item {
      padding: 10px 10px;
      min-width: 80px;
    }
  }
  .profile-card-inf__title {
    font-family: fantasy;
    font-weight: 700;
    font-size: 20px;
    color: #324e63;
  }
  .profile-card-inf__txt {
    font-weight: 500;
    margin-top: 10px;
  }
  .profile-card-ctr {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 40px;
  }
  @media screen and (max-width: 576px) {
    .profile-card-ctr {
      flex-wrap: wrap;
    }
  }
  .profile-card__button {
    background: none;
    border: none;
    font-family: 'Quicksand', sans-serif;
    font-weight: 700;
    font-size: 19px;
    margin: 15px 35px;
    padding: 15px 40px;
    min-width: 201px;
    border-radius: 50px;
    min-height: 55px;
    color: #fff;
    cursor: pointer;
    backface-visibility: hidden;
    transition: all .3s;
  }
  @media screen and (max-width: 768px) {
    .profile-card__button {
      min-width: 170px;
      margin: 15px 25px;
    }
  }
  @media screen and (max-width: 576px) {
    .profile-card__button {
      min-width: inherit;
      margin: 0;
      margin-bottom: 16px;
      width: 100%;
      max-width: 300px;
    }
    .profile-card__button:last-child {
      margin-bottom: 0;
    }
  }
  .profile-card__button:focus {
    outline: none !important;
  }
  @media screen and (min-width: 768px) {
    .profile-card__button:hover {
      transform: translateY(-5px);
    }
  }
  .profile-card__button:first-child {
    margin-left: 0;
  }
  .profile-card__button:last-child {
    margin-right: 0;
  }
  .profile-card__button.button--blue {
    background: linear-gradient(45deg, #1da1f2, #0e71c8);
    box-shadow: 0px 4px 30px rgba(19, 127, 212, 0.4);
  }
  .profile-card__button.button--blue:hover {
    box-shadow: 0px 7px 30px rgba(19, 127, 212, 0.75);
  }
  .profile-card__button.button--orange {
    background: linear-gradient(45deg, #d5135a, #f05924);
    box-shadow: 0px 4px 30px rgba(223, 45, 70, 0.35);
  }
  .profile-card__button.button--orange:hover {
    box-shadow: 0px 7px 30px rgba(223, 45, 70, 0.75);
  }

  .profile-card__button.button--green {
    background: linear-gradient(45deg, #8bea7ebf, #3dab3b);
    box-shadow: 0px 4px 30px rgba(140, 195, 76, 0.35);
  }
  .profile-card__button.button--green:hover {
    box-shadow: 0px 7px 30px rgba(140, 195, 76, 0.75);
  }

  .profile-card textarea {
    width: 100%;
    resize: none;
    height: 210px;
    margin-bottom: 20px;
    border: 2px solid #dcdcdc;
    border-radius: 10px;
    padding: 15px 20px;
    color: #324e63;
    font-weight: 500;
    font-family: 'Quicksand', sans-serif;
    outline: none;
    transition: all .3s;
  }
  .profile-card textarea:focus {
    outline: none;
    border-color: #8a979e;
  }

  /* 考核展开 */
  .popup{
    width: 100%;
    height: 100vh;
    display: none;
    position: fixed;
    top: 0;
    right: 0;
  }

  #popup-article:target{
    display: block;
  }

  .popup__block{
    height: calc(100vh - 40px);
    padding: 5% 15%;
    box-sizing: border-box;
    margin-top: 20px;
    overflow: auto;
    background-color: #eee;
    -webkit-animation: fade .5s ease-out 1.3s both;
    animation: fade .5s ease-out 1.3s both;
  }

  .popup:before{
    content: "";
    box-sizing: border-box;
    width: 100%;
    box-shadow: inset 0 0 0 20px #f0f0f0;
    background: #fff;
    position: fixed;
    top: 50%;
    will-change: height, top;
    -webkit-animation: open-animation .6s cubic-bezier(0.83, 0.04, 0, 1.16) .65s both;
    animation: open-animation .6s cubic-bezier(0.83, 0.04, 0, 1.16) .65s both;
  }

  .popup:after{
    content: "";
    width: 0;
    height: 2px;
    background-color: #f0f0f0;
    will-change: width, opacity;
    -webkit-animation: line-animation .6s cubic-bezier(0.83, 0.04, 0, 1.16) both;
    animation: line-animation .6s cubic-bezier(0.83, 0.04, 0, 1.16) both;
    position: absolute;
    top: 50%;
    left: 0;
    margin-top: -1px;
  }

  @-webkit-keyframes line-animation{
    0%{
      width: 0;
      opacity: 1;
    }
    99%{
      width: 100%;
      opacity: 1;
    }
    100%{
      width: 100%;
      opacity: 0;
    }
  }

  @keyframes line-animation{
    0%{
      width: 0;
      opacity: 1;
    }
    99%{
      width: 100%;
      opacity: 1;
    }
    100%{
      width: 100%;
      opacity: 0;
    }
  }

  @-webkit-keyframes open-animation{
    0%{
      height: 0;
      top: 50%;
    }
    100%{
      height: 100vh;
      top: 0;
    }
  }

  @keyframes open-animation{
    0%{
      height: 0;
      top: 50%;
    }
    100%{
      height: 100vh;
      top: 0;
    }
  }

  @-webkit-keyframes fade{
    0%{
      opacity: 0;
    }
    100%{
      opacity: 1;
    }
  }

  @keyframes fade{
    0%{
      opacity: 0;
    }
    100%{
      opacity: 1;
    }
  }
  .popup__close{
    width: 3.2rem;
    height: 3.2rem;
    text-indent: -9999px;
    position: absolute;
    background-repeat: no-repeat;
    background-position: center center;
    background-size: contain;
    background-image: url(data:image/svg+xml;base64,PHN2ZyBmaWxsPSIjMDAwMDAwIiBoZWlnaHQ9IjI0IiB2aWV3Qm94PSIwIDAgMjQgMjQiIHdpZHRoPSIyNCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4gICAgPHBhdGggZD0iTTE5IDYuNDFMMTcuNTkgNSAxMiAxMC41OSA2LjQxIDUgNSA2LjQxIDEwLjU5IDEyIDUgMTcuNTkgNi40MSAxOSAxMiAxMy40MSAxNy41OSAxOSAxOSAxNy41OSAxMy40MSAxMnoiLz4gICAgPHBhdGggZD0iTTAgMGgyNHYyNEgweiIgZmlsbD0ibm9uZSIvPjwvc3ZnPg==);
  }
  .popup__media{
    margin-bottom: 2rem;
  }

  @media screen and (min-width: 641px){
    .popup__close{
      top: 40px;
      right: 40px;
    }
    .popup__media{
      max-width: 35%;
    }
    .popup__media_left{
      float: left;
      margin-right: 3rem;
    }
    .popup__media_right{
      float: right;
      margin-left: 3rem;
    }
  }

  @media screen and (max-width: 640px){
    .popup__close{
      top: 20px;
      right: 20px;
    }
  }



  /* ===== */
  .bottom {
    .right{
      .el-button{
        color: #409EFF;
        border-color: #c6e2ff;
        background-color: #ecf5ff;
      }
    }
  }
  .right {
    margin-left: auto;
  }
  .inner .contenti .question {
    margin-left: 40px;
    color: #9a9a9a;
    font-size: 14px;
  }
  .content .inner .titlei {
    margin-left: 20px;
    font-size: 16px;
    color: #88949b;
    font-weight: bold;
  }
  .content .title .time {
    font-size: 16px;
    margin-left: 420px;
    color: #999;
  }
  .content .stitle {
    background-color: #0195ff;
  }
  .content .title span {
    margin-right: 10px;
  }
  #msg .content .title {
    font-size: 20px;
    margin: 0px;
    display: flex;
    align-items: center;
  }
  .content {
    margin-top: 20px;
    background-color: #fff;
  }
  .content .header {
    padding: 10px 30px;
  }
  .wrapper .info {
    margin: 20px 0px 0px 20px;
    border-top: 1px solid #eee;
    padding: 20px 0px 10px 0px;
  }
  .wrapper .info a {
    color: #88949b;
    font-size: 14px;
  }
  .wrapper .info a:hover {
    color: #0195ff;
  }
  .wrapper .bottom .btn {
    cursor: pointer;
    padding: 5px 10px;
    border: 1px solid #88949b;
    border-radius: 4px;
  }
  .wrapper .bottom {
    display: flex;
    margin-left: 20px;
    color: #999;
    font-size: 14px;
    align-items: center;
  }
  .wrapper .bottom li {
    margin-right: 14px;
  }
  #msg {
    background-color: #eee;
    width: 980px;
    margin: 0 auto;
  }
  #msg .title {
    margin: 20px;
  }
  #msg .wrapper {
    background-color: #fff;
    padding: 10px;
  }
  .wrapper .top {
    display: flex;
    margin: 20px;
    align-items: center;
  }
  .wrapper .top .right {
    margin-left: auto;
  }
  .wrapper .top .example {
    color: #333;
    font-size: 22px;
    font-weight: 700;
  }
  .wrapper .top li i {
    margin-left: 20px;
    color: #88949b;
  }
  .wrapper .right .count {
    margin-right: 60px;
    color: #fff;
    padding: 4px 10px;
    background-color: #88949b;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
    border: 1px solid #88949b;
  }
  .wrapper .right .score {
    position: absolute;
    left: 53px;
    top: -5px;
    padding: 1px 12px;
    font-size: 20px;
    color: #88949b;
    border: 1px dashed #88949b;
    border-left: none;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    font-weight: bold;
  }
  .wrapper .right div {
    position: relative;
  }

  .btn-disabled {
    color: #c0c4cc !important;
    cursor: not-allowed !important;
    background-image: none !important;
    background-color: #fff !important;
    border-color: #ebeef5 !important;
  }

</style>

