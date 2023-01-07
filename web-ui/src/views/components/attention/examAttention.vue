<template>
  <div id="attention" :exam="exam">
    <div class="attention-wrapper">
      <div style="margin: 20px 20px -100px 20px;">
        <el-avatar :size="100" :src="exam.coverImg"></el-avatar>
      </div>
      <ul class="top">
        <li class="example">{{exam.title}}</li>
        <li class="right">
          <div>
            <span class="count">总分</span>
            <span class="score">{{exam.totalScore}}</span>
          </div>
        </li>
      </ul>
      <div style="margin-top: 50px;width: 90%;margin-left: 5%;">
        <el-divider content-position="center">考生须知</el-divider>
        <div style="min-height: 400px;">
          <ol style="background: #f3f3f3;color: #888;padding: 20px 20px 20px 40px;list-style: decimal;">
            <li>本试卷为练习型试卷，您可以参加模拟该试卷；</li>
            <li>提交试卷（手动提交试卷或者到时自动提交试卷）后无法重新答题，您必须再次重新报名参加</li>
            <li>若出现网络波动、系统异常、提交失败等情况，请及时通知管理员进行处理；</li>
          </ol>
          <div class="profile-card-ctr">
            <a v-if="countDown > 0" class="profile-card__button button--blue js-message-btn" style="background: #9E9E9E;cursor: no-drop;">请仔细阅读协议<span>{{'（' + countDown + '秒）'}}</span></a>
            <a v-else @click="beginExamine" class="profile-card__button button--blue js-message-btn">去练习</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import {checkBeforeExamine} from "@/api/edu/examRecord";
export default {
  props: {
    exam: {
      type: Object,
      default: () => {
        return {};
      }
    }
  },
  data() {
    return {
      countDown: 5,
      countDownInterval: null
    }
  },
  mounted() {
    const _this = this
    _this.countDownInterval = setInterval(() => {
      if (_this.countDown > 0){
        _this.countDown--
      } else {
        window.clearInterval(_this.countDownInterval)
        _this.countDownInterval = null
      }
    }, 1000)
  },
  beforeDestroy() {
    if (this.countDownInterval){
      window.clearInterval(this.countDownInterval)
      this.countDownInterval = null
    }
  },
  methods: {
    beginExamine(){
      const recordCollectId = this.exam.examineRecord.recordCollectId
      checkBeforeExamine(recordCollectId).then(res => {
        this.$router.push({path: `/examine/${recordCollectId}`})
      })
    },

  }
}
</script>


<style lang="scss" scoped>
.bottom {
  .right{
    .el-button{
      color: #409EFF;
      border-color: #c6e2ff;
      background-color: #ecf5ff;
    }
  }
}
#attention ul {
  list-style: none;
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
#attention .content .title {
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
.attention-wrapper .bottom .btn {
  cursor: pointer;
  padding: 5px 10px;
  border: 1px solid #88949b;
  border-radius: 4px;
}
.attention-wrapper .bottom {
  display: flex;
  margin: 20px 16px 20px 100px;
  color: #999;
  font-size: 14px;
  align-items: center;
}
.attention-wrapper .bottom li {
  margin-right: 14px;
}
#attention {
  background-color: #eee;
  //width: 980px;
  margin: 0 auto;
}
#attention .title {
  margin: 20px;
}
#attention .attention-wrapper {
  background-color: #fff;
  padding: 10px;
}
.attention-wrapper .top {
  display: flex;
  margin: 20px 16px 20px 100px;
  align-items: center;
}
.attention-wrapper .top .right {
  margin-left: auto;
}
.attention-wrapper .top .example {
  color: #333;
  font-size: 22px;
  font-weight: 700;
}
.attention-wrapper .top li i {
  margin-left: 20px;
  color: #88949b;
}
.attention-wrapper .right .count {
  margin-right: 60px;
  color: #fff;
  padding: 4px 10px;
  background-color: #88949b;
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
  border: 1px solid #88949b;
}
.attention-wrapper .right .score {
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
.attention-wrapper .right div {
  position: relative;
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
  text-align: center;
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
</style>
