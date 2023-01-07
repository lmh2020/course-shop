<!--考生答题界面-->
<template>
  <div id="answer">
    <!--顶部信息栏-->
    <div class="top">
      <ul class="item">
        <li><i :class="slider_flag ? 'el-icon-s-fold' : 'el-icon-s-unfold'" ref="toggle" @click="slider_flag = !slider_flag" style="line-height: 0.8;font-size: 28px;"></i></li>
        <li>{{exam.title}}</li>
        <li v-show="automaticSubmitProcessing" style="display: block;margin: -15px auto;">
          <el-alert title="系统自动保存" type="info" description="后台正在自动保存您当前的试题答案..." show-icon style="height: 50px;"/>
        </li>
      </ul>
    </div>

    <!-- 考核主体 -->
    <div class="flexarea">

      <!--左边题目编号区-->
      <transition name="slider-fade">
        <div class="left" v-if="slider_flag">
          <ul class="l-top">
            <li>
              <a href="javascript:;"></a>
              <span>当前</span>
            </li>
            <li>
              <a href="javascript:;"></a>
              <span>未答</span>
            </li>
            <li>
              <a href="javascript:;"></a>
              <span>已答</span>
            </li>
            <li>
              <a href="javascript:;"></a>
              <span>标记</span>
            </li>
          </ul>
          <div class="l-bottom">
            <div v-for="(questions, questionType) of groupQuestions" :key="questionType" class="item">
              <p>{{questions[0].questionType.desc}}</p>
              <ul>
                <li v-for="(question, index) in questions" :key="index">
                  <a @click="selectQuestion(question)" href="javascript:;" :class="{'mark-current': question.id == selectedQuestion.id, 'mark-answered' : answers[question.id].length}">
                    <span :class="{'mark-flag': markQuestions.includes(question.id)}"></span>{{question.sort}}
                  </a>
                </li>
              </ul>
            </div>
            <div @click="preSubmitExam" class="final">交卷</div>
          </div>
        </div>
      </transition>

      <!--右边选择答题区-->
      <transition name="slider-fade">
        <div class="right">
          <!-- 顶部 -->
          <div class="title">
            <p style="font-size: 18px;">{{selectedQuestion.questionType.desc}}</p>
            <i class="iconfont icon-right auto-right"></i>
          </div>
          <!-- 中间 -->
          <div class="content">
            <div v-if="true" style="padding-top: 20px;">
              <!-- 试题顶部 -->
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <el-row :gutter="20">
                    <el-col :span="16"><div>
                      <el-tag effect="dark" style="margin-right: 10px;">{{selectedQuestion.sort}}</el-tag>
                      <span v-html="$options.filters.questionFormat(selectedQuestion.question)" style="font-weight: bolder;font-size: 20px;"></span>
                    </div></el-col>
                    <el-col :span="8">
                      <div class="question-bar"><span class="q-count">分数</span><span class="q-score">{{selectedQuestion.score}}</span></div>
                    </el-col>
                  </el-row>
                </div>
                <!-- 试题内容 -->
                <div class="question-content">
                  <div v-if="['SINGLE_CHOICE', 'JUDGE'].includes(selectedQuestion.questionType.code)">
                    <div>
                      <el-radio-group v-model="answers[selectedQuestion.id][0]">
                        <el-radio v-for="(item, index) in selectedQuestion.answers" :key="index" :label="item.key">{{item.val}}</el-radio>
                      </el-radio-group>
                    </div>
                  </div>
                  <div v-else-if="selectedQuestion.questionType.code == 'MULTIPLE_CHOICE'">
                    <el-checkbox-group v-model="answers[selectedQuestion.id]">
                      <el-checkbox v-for="(item, index) in selectedQuestion.answers" @change="answerChange" :label="item.key">{{item.val}}</el-checkbox>
                    </el-checkbox-group>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
          <!-- 下方操作按钮 -->
          <div class="operation">
            <ul class="end">
              <li @click="JumpToPrevQuestion" class="un-select"><i class="el-icon-arrow-left"></i><span>上一题</span></li>
              <li v-if="markQuestions.includes(selectedQuestion.id)" @click="cancelMarkCurrentQuestion" class="un-select" style="background-color: #ffc107"><i class="el-icon-s-release"></i><span style="padding-top: 5px;">取消</span></li>
              <li v-else @click="markCurrentQuestion" class="un-select"><i class="el-icon-s-flag"></i><span style="padding-top: 5px;">标记</span></li>
              <li @click="JumpToNextQuestion" class="un-select"><span>下一题</span><i class="el-icon-arrow-right"></i></li>
            </ul>
          </div>
        </div>
      </transition>
    </div>

    <!-- 交卷确认弹窗 -->
    <el-dialog title="交卷确认" :visible.sync="submitExamDialogOpen" @close="closeSubmitExamDialog" destroy-on-close width="55%" append-to-body>
      <el-alert v-if="!unfinishedQuestions.length" title="提示" type="success" description="恭喜您已经答完了该考核的所有试题！确认交卷吗？" show-icon effect="dark"/>
      <div v-else>
        <el-alert title="提示" :description="'您当前剩余 ' + unfinishedQuestions.reduce((count, curr) => count + curr.questions.length, 0) + ' 道试题未完成！请检查'" type="warning" show-icon effect="dark" style="width: 96%;margin: 0px 0 2% 2%;"/>
        <el-table :data="unfinishedQuestions" border stripe>
          <el-table-column prop="questionType.desc" align="center" label="试题类型" width="180"/>
          <el-table-column prop="questions" align="center" label="未答试题">
            <template slot-scope="scope">
              <div class="l-bottom" style="background-color: transparent;">
                <div class="item">
                  <ul>
                    <li v-for="(question, index) in scope.row.questions" :key="index">
                      <el-popconfirm @onConfirm="continueExamine(question)" title="您可以点击按钮直接跳转到该试题" confirmButtonText='去答题' cancelButtonText='关闭' icon="el-icon-info" iconColor="green" placement="top">
                        <a slot="reference" href="javascript:;"><span></span>{{question.sort}}</a>
                      </el-popconfirm>
                    </li>
                  </ul>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column align="center" label="所占分值" width="150">
            <template slot-scope="scope">
              <el-tag>{{`${scope.row.totalScore}分`}}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer" style="text-align: center;">
        <el-button @click="closeSubmitExamDialog">继续答题</el-button>
        <el-button type="primary" @click="submitExam('FALSE')">确定交卷</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

import {examineDetail} from "@/api/edu/exam";
import {submitExamine} from "@/api/edu/examRecord";

export default {
  data() {
    return {
      exam: {},//当前考试信息
      questionCount: 0,//试题总数
      recordCollectId: this.$route.params.recordCollectId,//考试报名记录的ID
      autoSubmitInterval: null,//自动保存定时器
      groupQuestions: {},
      selectedQuestion: {
        questionType: {},
      },//当前选中的试题

      answers: {},//用户填写的答案数据（key为试题编号，val为试题答案（注意：为了方便处理数据，答案数据格式统一为数组，哪怕只有一个答案也用数组，包括主观题））
      markQuestions: [],
      slider_flag: true, //左侧显示隐藏标识符
      submitExamDialogOpen: false,
      unfinishedQuestions: [],//未答的试题数组
      automaticSubmitTime: 30 * 1000,//定时保存时间间隔
      automaticSubmitProcessing: false,//后台正在自动保存答案
      $submitNotifyMessage: null,//自动交卷倒计时弹窗对象
    }
  },
  created() {
    const _this = this
    _this.getExamData()
  },
  mounted() {
    window.addEventListener('beforeunload', this.beforeunloadHandler)
  },
  destroyed() {
    window.removeEventListener('beforeunload', this.beforeunloadHandler)
    /* 自动保存定时器 */
    if (this.autoSubmitInterval){
      window.clearInterval(this.autoSubmitInterval)
      this.autoSubmitInterval = null
    }
  },
  filters: {
    questionFormat (question){
      return `<span>${question || ''}</span>`
    }
  },
  methods: {
    /* 获取试卷试题信息 */
    getExamData() { //获取当前试卷所有信息
      const _this = this
      examineDetail(_this.$route.params.recordCollectId).then(res => {
        const data = res.data
        _this.exam = data.exam
        _this.questionCount = data.questionCount
        _this.groupQuestions = data.questions
        for(let key in _this.groupQuestions){
          if (!_this.selectedQuestion.id){
            _this.selectedQuestion = _this.groupQuestions[key][0]
          }
          for (let question of _this.groupQuestions[key]){
            _this.$set(_this.answers, question.id, [])//上面的方法不会触发更新，原因未知
          }
        }

        /* 如果考核状态为进行中（说明不是第一次调用该方法获取考核数据，比如页面刷新过），则获取一下上次缓存的用户试题答案记录，防止答案丢失 */
        if (data.exam.examineRecord.examRecordState.code === 'UNDERWAY' && data.answersCache){
          /* 注意：设置答案的代码一定要等待groupQuestions（试题信息）先去加载试题DOM，加载完毕后再赋值，否则不会生效 */
          _this.$nextTick(() => {
            _this.answers = data.answersCache
          })
        }

        /* 定时自动保存定时器 */
        setTimeout(() => {
          _this.autoSubmitInterval = setInterval(() => {
            _this.submitExam('TRUE')
          }, _this.automaticSubmitTime)
        }, _this.automaticSubmitTime)

      }).catch(error => {
        this.$router.push({ path: '/examError', query: { msg: error.message }})
      })
    },

    /* 预交卷，先做校验、提醒 */
    preSubmitExam(){
      /* 找出所有未答的题目 */
      const _this = this
      for(let key in _this.groupQuestions){
        const questions = []
        for (let question of _this.groupQuestions[key]){
          const questionQnswer = _this.answers[question.id]
          if (!questionQnswer || !questionQnswer.length){
            questions.push(question)
          }
        }
        if (questions.length){
          _this.unfinishedQuestions.push({
            questionType: questions[0].questionType,
            questions: questions,
            totalScore: questions.reduce((sum, curr) => {
              return sum + curr.score
            }, 0)
          })
        }
      }
      _this.submitExamDialogOpen = true
    },

    /* 确认提交考核 */
    submitExam(automaticSubmit){
      const _this = this
      let loading
      if (automaticSubmit == 'FALSE'){
        loading = this.$loading({
          lock: true,
          text: '正在保存答案信息！请稍后...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        });
      } else {
        _this.automaticSubmitProcessing = true
      }
      submitExamine({
        recordCollectId: _this.recordCollectId,
        automaticSubmit: automaticSubmit,
        userAnswers: _this.answers
      }).then(res => {
        if (automaticSubmit == 'FALSE'){
          /* 注意：最终交卷成功一定要关闭自动保存的定时器，不然可能会覆盖已提交的答案 */
          if (_this.autoSubmitInterval){
            window.clearInterval(_this.autoSubmitInterval)
            _this.autoSubmitInterval = null
          }
          window.removeEventListener('beforeunload', _this.beforeunloadHandler)

          _this.msgSuccess("交卷成功");
          _this.$router.push("/education/examList")
        }
      }).finally(() => {
        if (loading){
          loading.close()
        }
        _this.automaticSubmitProcessing = false
      })
    },

    /* 继续考试（返回考试界面） */
    continueExamine(question){
      const _this = this
      _this.closeSubmitExamDialog()
      if (question){
        setTimeout(() => {
          _this.selectQuestion(question)
        }, 500)
      }
    },

    /* 关闭提交考试弹窗 */
    closeSubmitExamDialog(){
      this.unfinishedQuestions = []
      this.submitExamDialogOpen = false
    },

    /* 选中试题 */
    selectQuestion(question){
      this.selectedQuestion = question
    },

    /* 标记试题 */
    markCurrentQuestion(){
      const currentQuestionId = this.selectedQuestion.id
      if (!this.markQuestions.includes(currentQuestionId)){
        this.markQuestions.push(currentQuestionId)
      }
    },

    /* 取消标记试题 */
    cancelMarkCurrentQuestion(){
      const currentQuestionId = this.selectedQuestion.id
      const index = this.markQuestions.indexOf(currentQuestionId)
      if (index > -1){
        this.markQuestions.splice(index, 1)
      }
    },

    /* 跳转到上一道试题 */
    JumpToPrevQuestion(){
      /* 如果当前试题在当前分类下不是第一个，则直接跳转到当前分类的上一道试题 */
      if (this.selectedQuestion.sort > 1){
        this.selectedQuestion = this.groupQuestions[this.selectedQuestion.questionType.code].find(question => question.sort == this.selectedQuestion.sort - 1)
      } else {
        /* 如果当前试题排在第一位，需要跨分类寻找排在上一个分类下的最后一道试题 */
        let prevKey
        const keys = Object.keys(this.groupQuestions)
        for (let i = keys.length - 1; i > 0; i--) {
          if (keys[i] == this.selectedQuestion.questionType.code){
            prevKey = keys[i - 1]
            break
          }
        }
        /* 没找到上一个试题类型，说明当前类型已排在第一位 */
        let prevQuestions
        if (!prevKey || (!(prevQuestions = this.groupQuestions[prevKey]) && !this.groupQuestions[prevKey].length)){
          return this.msgInfo("已经没有上一道试题！！！");
        }
        this.selectedQuestion = prevQuestions[prevQuestions.length - 1]
      }
    },

    /* 跳转到下一题 */
    JumpToNextQuestion(){
      /* 如果当前试题在当前分类下不是最后，则直接跳转到当前分类的下一道试题 */
      const group = this.groupQuestions[this.selectedQuestion.questionType.code]
      if (this.selectedQuestion.sort < group.length){
        this.selectedQuestion = group.find(question => question.sort == this.selectedQuestion.sort + 1)
      } else {
        /* 如果当前试题排在最后一位，需要跨分类寻找排在下一个分类的第一道试题 */
        let nextKey
        const keys = Object.keys(this.groupQuestions)
        for (let i = 0; i < keys.length; i++) {
          if (keys[i] == this.selectedQuestion.questionType.code){
            nextKey = keys[i + 1]
            break
          }
        }
        /* 没找到下一个试题类型，说明当前类型已排在最后一位 */
        let nextQuestions
        if (!nextKey || (!(nextQuestions = this.groupQuestions[nextKey]) && !this.groupQuestions[nextKey].length)){
          return this.msgInfo("已经没有下一道试题！！！");
        }
        this.selectedQuestion = nextQuestions[0]
      }
    },

    answerChange(val){
      /* 遍历当前试题是否没写答案 */
      const emptyAnswer = this.answers[this.selectedQuestion.id].find(answer => {
        return answer && answer.trim()
      })
      if (!emptyAnswer){
        this.answers[this.selectedQuestion.id] = []
      }
    },

    beforeunloadHandler(e){
      e.returnValue = "确定要离开当前页面吗？这可能会丢失部分已填写的答案"
    }

  },
}
</script>

<style lang="scss">

  * {
    margin: 0;
    padding: 0;
  }

  ul {
    list-style: none;
  }

  .iconfont.icon-time {
    color: #2776df;
    margin: 0px 6px 0px 20px;
  }
  .analysis {
    margin-top: 20px;
    .right {
      color: #2776df;
      font-size: 18px;
      border: 1px solid #2776df;
      padding: 0px 6px;
      border-radius: 4px;
      margin-left: 20px;
    }
    ul li:nth-child(2) {
      margin: 20px 0px;
    }
    ul li:nth-child(3) {
      padding: 10px;
      background-color: #d3c6c9;
      border-radius: 4px;
    }
  }
  .analysis span:nth-child(1) {
    font-size: 18px;
  }
  .mark-flag {
    position: absolute;
    width: 8px;
    height: 8px;
    content: "";
    background-color: red;
    border-radius: 50%;
    top: -3px;
    left: 22px;
  }
  .mark-current {
    position: relative;
    border: 2px solid #ff1100 !important;
  }
  .mark-answered {
    background-color: #5188b8 !important;
  }
  .fill .el-input {
    display: inline-flex;
    width: 150px;
    margin-left: 20px;
    .el-input__inner {
      border: 1px solid transparent;
      border-bottom: 1px solid #eee;
      padding-left: 20px;
    }
  }
  /* slider过渡效果 */
  .slider-fade-enter-active {
    transition: all .3s ease;
  }
  .slider-fade-leave-active {
    transition: all .3s cubic-bezier(1.0, 0.5, 0.8, 1.0);
  }
  .slider-fade-enter, .slider-fade-leave-to {
    transform: translateX(-100px);
    opacity: 0;
  }

  .operation .end li:nth-child(2) {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: rgb(39, 118, 223);
    border-radius: 50%;
    width: 55px;
    height: 55px;
    color: #fff;
  }
  .operation .end li {
    cursor: pointer;
    margin: -5px 100px;
  }
  .operation {
    background-color: #fff;
    border-radius: 4px;
    padding: 10px 0px;
    margin-right: 10px;
    height: 10%;
  }
  .operation .end {
    display: flex;
    justify-content: center;
    align-items: center;
    color: rgb(39, 118, 223);
  }
  .content .number {
    display: inline-flex;
    justify-content: center;
    align-items: center;
    width: 20px;
    height: 20px;
    background-color: rgb(39, 118, 223);
    border-radius: 4px;
    margin-right: 4px;
  }
  .content {
    padding: 0px 20px;
  }
  .content .topic {
    padding: 20px 0px;
    padding-top: 30px;
  }
  .right .content {
    background-color: #fff;
    margin: 10px;
    margin-left: 0px;
    //height: 470px;
    height: 80%;
  }
  .content .el-radio-group label {
    color: #000;
    margin: 12px 0px;
  }
  .content .el-radio-group {
    display: flex;
    flex-direction:column;
  }

  .content .el-checkbox-group label {
    color: #000;
    margin: 12px 0px;
  }

  .content .el-checkbox-group {
    display: flex;
    flex-direction:column;
  }

  .right .title p {
    margin-left: 20px;
  }
  .flexarea {
    display: flex;
    height: 94%;
  }
  .flexarea .right {
    flex: 1;
  }
  .auto-right {
    margin-left: auto;
    color: #2776df;
    margin-right: 10px;
  }
  .right .title {
    margin-right: 10px;
    padding-right: 30px;
    display: flex;
    margin-top: 10px;
    background-color: #fff;
    //height: 50px;
    //height: 8%;
    line-height: 50px;
  }
  .clearfix {
    clear: both;
  }
  .l-bottom .final {
    font-size: 20px;
    cursor: pointer;
    display: block;
    text-align: center;
    background-color: rgb(39, 118, 223);
    width: 230px;
    margin: 0 auto;
    border-radius: 4px;
    height: 35px;
    line-height: 35px;
    color: #fff;
    margin-top: 30px;
  }
  #answer .left .item {
    margin-bottom: 15px;
    padding: 0px;
    font-size: 16px;
  }
  .l-bottom {
    height: 90%;
    border-radius: 4px;
    background-color: #fff;
  }
  .l-bottom .item p {
    margin-bottom: 15px;
    margin-top: 10px;
    color: #000;
    margin-left: 10px;
    letter-spacing: 2px;
  }
  .l-bottom .item li {
    width: 40px;
    margin-left: 7px;
    margin-bottom: 10px;
  }
  .l-bottom .item {
    display: flex;
    flex-direction: column;
  }
  .l-bottom .item ul {
    border-bottom: 1px solid #e4e4e4;
    width: 100%;
    margin-bottom: -8px;
    margin-left: 10px;
    display: flex;
    //justify-content: space-around;
    flex-wrap: wrap;
  }
  .l-bottom .item ul li a {
    position: relative;
    justify-content: center;
    display: inline-flex;
    align-items: center;
    width: 35px;
    height: 35px;
    border-radius: 50%;
    background-color: #fff;
    border: 1px solid #9a9a9a;
    text-align: center;
    color: #000;
    font-size: 18px;
  }
  .left .l-top {
    height: 8%;
    display: flex;
    justify-content: space-around;
    padding: 16px 0px;
    border: 1px solid #eee;
    border-radius: 4px;
    margin-bottom: 10px;
    background-color: #fff;
  }
  .left {
    width: 300px;
    height: 100%;
    margin: 10px 10px 0px 10px;
  }
  .left .l-top li:nth-child(2) a {
    border: 1px solid #9a9a9a;
  }
  .left .l-top li:nth-child(3) a {
    background-color: #5188b8;
    border: none;
  }
  .left .l-top li:nth-child(4) a {
    position: relative;
    border: 1px solid #eee;
  }
  .left .l-top li:nth-child(4) a::before {
    width: 8px;
    height: 8px;
    content: " ";
    position: absolute;
    background-color: red;
    border-radius: 50%;
    top: -3px;
    left: 16px;
  }
  .left .l-top li {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
  }
  .left .l-top li a {
    width: 25px;
    height: 25px;
    display: inline-block;
    padding: 15px;
    border-radius: 50%;
    background-color: #fff;
    border: 1px solid #FF90AA;
  }
  #answer .top {
    min-height: 60px;
    height: 6%;
    background-color: rgb(39, 118, 223);
  }
  #answer .item {
    color: #fff;
    display: flex;
    padding: 20px;
    font-size: 20px;
  }
  #answer .top .item li:nth-child(1) {
    //margin-right: 10px;
  }
  #answer .top .item li:nth-child(3) {
    //position: relative;
    //margin-left: auto;
  }
  #answer {
    height: 100%;
    padding-bottom: 30px;
    background-color: #eee;
  }
  .icon20 {
    font-size: 20px;
    font-weight: bold;
  }
  .item .msg {
    padding: 10px 15px;
    border-radius: 4px;
    top: 47px;
    right: -30px;
    color: #6c757d;
    position: absolute;
    border: 1px solid rgba(0,0,0,.15);
    background-color: #fff;
  }
  .item .msg p {
    font-size: 16px;
    width: 200px;
    text-align: left;
  }


  /* 题目 */
  .question-bar {
    position: relative;
    float: right;
  }

  .question-bar .q-count {
    margin-right: 60px;
    color: #fff;
    padding: 4px 10px;
    background-color: #ff6767;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
    border: 1px solid #ff6767;
  }
  .question-bar .q-score {
    position: absolute;
    left: 50px;
    top: -5px;
    padding: 0px 12px;
    font-size: 21px;
    color: #88949b;
    border: 2px dashed #88949b;
    border-left: none;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    font-weight: bold;
  }

  .question-content {
    min-height: 250px;
    .el-row {
      padding: 0.3rem;
    }

    /* 试题选项字体大小 */
    span.el-radio__label {
      font-size: 18px;
    }

    .el-radio__inner {
      width: 20px;
      height: 20px;
    }
    .el-radio__inner::after{
      width: 6px;
      height: 6px;
    }
    .el-checkbox__inner{
      width: 20px;
      height: 20px;
    }
    .el-checkbox__inner::after{
      width: 8px;
      height: 8px;
      left: 4px;
      top: 2px;
    }
    .el-input{
      font-size: 18px;
    }
    .el-textarea {
      font-size: 18px;
      .el-input__count{
        font-size: 18px;
      }
    }

    span.el-checkbox__label{
      font-size: 18px;
    }
  }

  /* 填空下划线效果 */
  .under-break {
    color: #8BC34A;
    white-space: pre;
    table-layout: fixed;
    word-break: break-all;
    display: inline;
    border-bottom: 1px solid #000000;
  }

  /* 不能选中 */
  .un-select {
    -moz-user-select:none;		/* 火狐 */
    -webkit-user-select:none;	/* 谷歌 */
    -ms-user-select:none;		/* IE */
    user-select:none;
  }

</style>
