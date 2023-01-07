<!--考生答题界面-->
<template>
  <div id="answer">
    <!--顶部信息栏-->
    <div class="top">
      <ul class="item">
        <li><el-page-header @back="$router.go(-1);" :content="exam.title"/></li>
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
          </ul>
          <div class="l-bottom">
            <div v-for="(questions, questionType) of groupQuestions" :key="questionType" class="item">
              <p>{{questions[0].questionType.desc}}</p>
              <ul>
                <li v-for="(question, index) in questions" :key="index">
                  <a @click="selectQuestion(question)" href="javascript:;" :class="{'mark-current': question.id == selectedQuestion.id, 'mark-answered' : !question.isEmptyAnswer()}">
                    {{question.sort}}
                  </a>
                </li>
              </ul>
            </div>
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
                  <div v-if="['SINGLE_CHOICE', 'JUDGE', 'MULTIPLE_CHOICE'].includes(selectedQuestion.questionType.code)">
                    <div>
                      <el-row v-for="(answer, index) in selectedQuestion.answers">
                        <el-tag v-if="selectedQuestion.userAnswer.includes(answer.key)" :key="answer.key" type="success" effect="dark">{{answer.key}}</el-tag>
                        <el-tag v-else type="info" :key="answer.key">{{answer.key}}</el-tag>
                        <span style="margin-left: 15px;">{{answer.val}}</span>
                      </el-row>
                    </div>
                  </div>
                </div>
              </el-card>

              <el-card class="box-card analysis" style="margin-top: 25px;">
                <div slot="header" class="clearfix">
                  <el-row>
                    <el-col :span="17"><div>
                      <el-tag>答案</el-tag>
                    </div></el-col>
                    <el-col :span="3">
                      <div class="question-bar"><span class="q-result-count">结果</span><span class="q-score">
                        <i :class="{'el-icon-success': selectedQuestion.result === 'CORRECT', 'el-icon-error': selectedQuestion.result === 'INCORRECT',
                        'el-icon-info': selectedQuestion.result === 'PART-CORRECT', 'el-icon-loading': !selectedQuestion.result}"/></span></div>
                    </el-col>
                    <el-col :span="3">
                      <div class="question-bar"><span class="q-final-count">得分</span><span class="q-score">{{selectedQuestion.userScore}}</span></div>
                    </el-col>
                  </el-row>
                </div>

                <el-row  :gutter="100">
                  <el-col :span="8">
                    <el-divider content-position="center"><el-tag effect="dark"><i class="el-icon-user-solid"/> 您的答案</el-tag></el-divider>
                    <el-alert :title="(selectedQuestion.userAnswer || []).toString()" type="info" :closable="false"/>
                  </el-col>
                  <el-col :span="8">
                    <el-divider content-position="center"><el-tag type="success" effect="dark"><i class="el-icon-s-opportunity"/> 正确答案</el-tag></el-divider>
                    <el-alert :title="(selectedQuestion.correctAnswer || []).toString()" type="info" :closable="false"/>
                  </el-col>
                  <el-col :span="8">
                    <el-divider content-position="center"><el-tag type="warning" effect="dark"><i class="el-icon-info"/> 答案解析</el-tag></el-divider>
                    <el-alert :title="selectedQuestion.analysis" type="info" :closable="false"/>
                  </el-col>
                </el-row>
              </el-card>
            </div>
          </div>
          <!-- 下方操作按钮 -->
          <div class="operation">
            <ul class="end">
              <li @click="JumpToPrevQuestion" class="un-select"><i class="el-icon-arrow-left"></i><span>上一题</span></li>
              <li @click="JumpToNextQuestion" class="un-select"><span>下一题</span><i class="el-icon-arrow-right"></i></li>
            </ul>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>

import {examineResult} from "@/api/edu/exam";

export default {
  data() {
    return {
      exam: {},//当前考试信息
      questionCount: 0,//试题总数
      recordCollectId: this.$route.params.recordCollectId,//考试报名记录的ID
      groupQuestions: {},

      selectedQuestion: {
        questionType: {},
      },//当前选中的试题
      slider_flag: true, //左侧显示隐藏标识符
    }
  },
  created() {
    const _this = this
    _this.getExamData()
  },
  destroyed() {

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
      examineResult(_this.$route.params.recordCollectId).then(res => {
        const data = res.data
        _this.exam = data.exam
        _this.questionCount = data.questionCount
        _this.groupQuestions = data.questions
        const recordSections = data.exam.recordSections
        for(let key in _this.groupQuestions){
          if (!_this.selectedQuestion.id){
            _this.selectedQuestion = _this.groupQuestions[key][0]
          }
          for (let question of _this.groupQuestions[key]){
            const section = recordSections.find(section => section.examQuestionId == question.id)
            if (section){
              question.userAnswer = JSON.parse(section.userAnswer || "[]")
              question.userScore = section.userScore == 0 || (section.userScore && Number(section.userScore) > 0) ? section.userScore : '未批改'
              question.result = (section.result || {}).code
              question.isEmptyAnswer = () => {
                if (question.userAnswer && question.userAnswer.length && question.userAnswer.find(answer => answer && answer.trim())){
                  return false;
                }
                return true
              }
            }
          }
        }
      }).catch(error => {
        this.$router.push({ path: '/examError', query: { msg: error.message }})
      })
    },

    /* 选中试题 */
    selectQuestion(question){
      this.selectedQuestion = question
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
    span.el-tag,.el-alert__title {
      font-size: 18px;
    }
    .el-alert {
      margin: 0 auto;
      width: fit-content;
    }
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
    padding: 20px;
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
  .left {
    width: 300px;
    height: 100%;
    margin: 10px 10px 0px 10px;
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
  .left .l-top li:nth-child(2) a {
    border: 1px solid #9a9a9a;
  }
  .left .l-top li:nth-child(3) a {
    background-color: #5188b8;
    border: none;
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
  #answer {
    position: absolute;
    height: 100%;
    padding-bottom: 30px;
    background-color: #eee;

    .el-page-header__title {
      font-size: 18px;
    }

    .el-page-header__content{
      color: white;
    }
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
  .question-bar .q-result-count {
    margin-right: 60px;
    color: #fff;
    padding: 4px 10px;
    background-color: #03A9F4;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
    border: 1px solid #03A9F4;
  }
  .question-bar .q-final-count {
    margin-right: 60px;
    color: #fff;
    padding: 4px 10px;
    background-color: #4CAF50;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
    border: 1px solid #4CAF50;
  }
  .question-bar .q-score {
    width: fit-content;
    min-height: 28px;
    position: absolute;
    left: 50px;
    top: -5px;
    padding: 0px 12px;
    font-size: 18px;
    color: #88949b;
    border: 2px dashed #88949b;
    border-left: none;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    font-weight: bold;
  }

  .question-content {
    min-height: 150px;
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
