<template>
  <div class="app-container">

    <!-- 表格搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="考核方向" prop="questionCategoryCodeArr">
        <el-select v-model="questionCategoryCodeArr" placeholder="请选择考核方向" multiple clearable size="small" style="width: 240px">
          <el-option v-for="mode in examModeOptions" :key="mode.dictValue" :label="mode.dictLabel" :value="mode.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item label="试题类型" prop="questionType">
        <el-select v-model="queryParams.questionType" placeholder="请选择试题类型" clearable size="small" style="width: 240px">
          <el-option v-for="questionType in questionTypeOptions" :key="questionType.dictValue" :label="questionType.dictLabel" :value="questionType.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item label="题目" prop="question">
        <el-input v-model="queryParams.question" placeholder="请输入题目" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="cyan" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['exam:question:store:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['exam:question:store:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['exam:question:store:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['exam:question:store:export']">导出</el-button>
      </el-col>
	  <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="questionList" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="考核方向" width="150" align="center" prop="questionCategoryCode">
        <template slot-scope="scope">
          <el-tag :color="(examModeOptions.find(item => item.dictValue == scope.row.questionCategoryCode) || {}).randomColor" style="color: white;">{{questionCategoryCodeFormat(scope.row)}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="试题类型" width="90" align="center" prop="questionType">
        <template slot-scope="scope">
          <el-tag :color="(questionTypeOptions.find(item => item.dictValue == scope.row.questionType) || {}).randomColor" style="color: white;">{{questionTypeFormat(scope.row)}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column show-overflow-tooltip label="题目" align="center" prop="question"/>
      <el-table-column label="分值" width="70" align="center" prop="score" />
      <el-table-column label="通过率" width="80" align="center" prop="totalUsedCount">
        <template slot-scope="scope">
          <el-tooltip placement="top">
            <div slot="content">通过：{{scope.row.totalReachedCount}}次<br/><br/>失败：{{scope.row.totalUsedCount - scope.row.totalReachedCount}}次</div>
            <span>{{scope.row.totalUsedCount > 0 ? Math.floor(scope.row.totalReachedCount * 100 / scope.row.totalUsedCount) + '%' : ''}}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="困难等级" width="130" align="center" prop="difficultyLevel">
        <template slot-scope="scope">
          <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
      <el-table-column label="操作" width="250" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row, scope.index)" v-hasPermi="['exam:question:store:edit']">预览</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['exam:question:store:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['exam:question:store:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 添加或修改考试试卷题目对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="60%" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编号" prop="id" hidden>
          <el-input v-model="form.id" disabled type="text" placeholder="编号"/>
        </el-form-item>
        <el-form-item label="考核方向" prop="questionCategoryCode">
          <el-select v-model="form.questionCategoryCode" placeholder="请选择考核方向" :disabled="form.id ? true : false">
            <el-option v-for="mode in examModeOptions" :key="mode.dictValue" :label="mode.dictLabel" :value="mode.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="试题类型" prop="questionType" disabled>
          <el-select @change="questionTypeChange" v-model="form.questionType" placeholder="请选择试题类型"  :disabled="form.id ? true : false">
            <el-option v-for="questionType in questionTypeOptions" :key="questionType.dictValue" :label="questionType.dictLabel" :value="questionType.dictValue" />
          </el-select>
        </el-form-item>

        <el-form-item label="题目" prop="question" v-if="form.questionType">
          <el-input v-model="form.question" type="textarea" placeholder="请输入题目" />
        </el-form-item>

        <el-form-item label="可选答案" prop="answers" v-if="form.questionType == 'SINGLE_CHOICE' || form.questionType == 'MULTIPLE_CHOICE'" class="is-required">
          <el-card class="box-card">
            <el-row :gutter="20" :key="answer.key" v-for="(answer, index) in form.answers" @mouseover.native="hoverQuestion(answer.key)" @mouseout.native="hoverQuestion(null)" style="margin-top: 1rem;">
              <el-col :span="22">
                <el-input placeholder="请输入选项" v-model="answer.val">
                  <template slot="prepend">{{answer.key}}</template>
                </el-input>
              </el-col>
              <el-col :span="2" v-show="hoverQuestionKey == answer.key">
                <i @click="removeNewAnswers(index)" class="el-icon-remove" style="font-size: 1.6rem; color: red;cursor: pointer;"></i>
              </el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top: 1rem;">
              <el-button @click="addNewAnswers" icon="el-icon-plus" type="primary" size="small" plain>添加选项</el-button>
            </el-row>
          </el-card>
        </el-form-item>
        <el-form-item label="可选答案" prop="answers" v-else-if="form.questionType == 'JUDGE'">
          <el-tag type="success" effect="dark">对</el-tag>
          <el-tag type="warning" effect="dark" style="margin-left: 10px;">错</el-tag>
        </el-form-item>

        <el-form-item label="正确答案" prop="correctAnswer" v-if="form.questionType == 'SINGLE_CHOICE'" class="is-required">
          <el-radio v-model="form.correctAnswer[0]" v-for="(answer, index) in form.answers" :label="answer.key" border>{{answer.key}}</el-radio>
        </el-form-item>
        <el-form-item label="正确答案" prop="correctAnswer" v-else-if="form.questionType == 'MULTIPLE_CHOICE'" class="is-required">
          <el-checkbox-group v-model="form.correctAnswer" size="medium">
            <el-checkbox v-for="(answer, index) in form.answers" :label="answer.key" border></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="正确答案" prop="correctAnswer" v-else-if="form.questionType == 'JUDGE'" class="is-required">
          <template>
            <el-radio v-model="form.correctAnswer[0]" label="A">对</el-radio>
            <el-radio v-model="form.correctAnswer[0]" label="B">错</el-radio>
          </template>
        </el-form-item>
        <el-form-item label="答案分析" prop="analysis" v-if="form.questionType">
          <el-input v-model="form.analysis" type="textarea" placeholder="请输入答案分析"  />
        </el-form-item>
        <el-form-item label="分值" prop="score" v-if="form.questionType">
          <el-input-number v-model="form.score" controls-position="right" :min="1" placeholder="请输入分值"/>
        </el-form-item>
        <el-form-item label="难度等级" prop="difficultyLevel" v-if="form.questionType" class="is-required">
          <el-rate v-model="form.difficultyLevel" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" style="line-height: 2.5;"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 预览详细 -->
    <el-dialog title="试题详情" :visible.sync="questionDetailDialogOpen" width="60%" append-to-body :destroy-on-close="true">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <el-row :gutter="20">
            <el-col :span="16"><div><i class="el-icon-question"/> 问题：
              <span>{{form.question}}</span>
            </div></el-col>
            <el-col :span="8">
              <span style="float: right; margin-left: 20px;"><el-rate :value="form.difficultyLevel" disabled/></span>
              <div class="question-bar"><span class="q-count">分数</span><span class="q-score">{{form.score}}</span></div>
            </el-col>
          </el-row>
        </div>
        <div class="question-content">
          <el-row v-for="answer in form.answersView">
            <el-tag v-if="form.correctAnswerView.indexOf(answer.key) > -1" type="success" effect="dark">{{answer.key}}</el-tag>
            <el-tag v-else type="info">{{answer.key}}</el-tag>
            <span style="margin-left: 15px;">{{answer.val}}</span>
          </el-row>
        </div>
      </el-card>

      <el-card class="box-card" style="margin-top: 20px;">
        <div slot="header" class="clearfix"><i class="el-icon-s-opportunity"/><span> 答案解析</span></div>
        <div>{{form.analysis}}</div>
      </el-card>
      <div>
        <el-divider content-position="left"><i class="el-icon-s-flag"></i> 被以下试卷引用</el-divider>
        <el-tag v-for="paper in referredPapers" effect="dark" style="margin-left: 10px;">{{ paper.title }}</el-tag>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="questionDetailDialogOpen = false;referredPapers = [];form = {};">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listStoreQuestion, getStoreQuestion, delStoreQuestion, addStoreQuestion, updateStoreQuestion, exportStoreQuestion, getExamQuestionReferredExams } from "@/api/edu/questionStore";
import CollapseTransition from 'element-ui/lib/transitions/collapse-transition';

export default {
  name: "question",
  comments: {CollapseTransition},
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 考核方向数据字典
      examModeOptions: [],
      //试题类型
      questionTypeOptions: [],
      // 总条数
      total: 0,
      // 考试试卷题目表格数据
      questionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      referredPapers: [],
      questionDetailDialogOpen: false,
      questionCategoryCodeArr: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        questionCategoryCodes: null,
        question: null,
        questionType: null,
      },
      // 表单参数
      form: {
        id: null,
        questionCategoryCode: [],
        answersView: [],
        correctAnswerView: [],
        question: "",
        questionType: {},
        answers: [],
        correctAnswer: [],
        analysis: null,
        score: null,
        sort: null,
      },
      /* 鼠标当前悬浮的答案key */
      hoverQuestionKey: null,
      // 表单校验
      rules: {
        questionCategoryCode: [
          { required: true, message: "考核方向不能为空", trigger: "change" }
        ],
        question: [
          { required: true, message: "题目不能为空", trigger: "blur" }
        ],
        questionType: [
          { required: true, message: "试题类型不能为空", trigger: "change" }
        ],
        score: [
          { required: true, message: "分值不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getMultiDicts(["edu_exam_mode", "edu_exam_question_type"]).then((response) => {
      const multiDicts = response.data
      this.examModeOptions = multiDicts.edu_exam_mode;
      this.fillRandomColors(this.examModeOptions)
      this.questionTypeOptions = multiDicts.edu_exam_question_type;
      this.fillRandomColors(this.questionTypeOptions)
    });
  },
  methods: {
    /** 查询考试试卷题目列表 */
    getList() {
      this.questionList = []
      this.loading = true;
      if (this.questionCategoryCodeArr && this.questionCategoryCodeArr.length){
        this.queryParams.questionCategoryCodes = this.questionCategoryCodeArr.join(",")
      } else {
        this.queryParams.questionCategoryCodes = null
      }

      listStoreQuestion(this.queryParams).then(response => {
        if (response.rows && response.rows.length){
          this.questionList = response.rows;
          for(let question of this.questionList){
            question.questionType = question.questionType.code
          }
        }
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        questionCategoryCodeArr: [],
        question: "",
        answersView: [],
        correctAnswerView: [],
        questionType: null,
        answers: null,
        correctAnswer: null,
        analysis: null,
        score: null,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加试题";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getStoreQuestion(id).then(response => {
        const data = response.data
        this.form = response.data;

        this.form = {
          id: data.id,
          questionCategoryCode: data.questionCategoryCode,
          questionCategoryCodeArr: [],
          question: data.question,
          answersView: [],
          correctAnswerView: [],
          questionType: data.questionType.code,
          answers: data.answers ? eval('(' + data.answers + ')') : [],
          correctAnswer: data.correctAnswer ? eval('(' + data.correctAnswer + ')') : [],
          analysis: data.analysis,
          score: data.score,
          difficultyLevel: data.difficultyLevel,
        };

        this.open = true;
        this.title = "修改试题";
      });
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const questionType = this.form.questionType
          if (questionType == 'SINGLE_CHOICE' || questionType == 'JUDGE' || questionType == 'MULTIPLE_CHOICE'){
            if (questionType != 'JUDGE' && this.form.answers.length < 2){
              return this.msgWarning("可选答案不能少于两项")
            } else if(!this.form.correctAnswer.length){
              return this.msgWarning("请选择正确答案")
            } else if(questionType == 'MULTIPLE_CHOICE' && this.form.correctAnswer.length < 2){
              return this.msgWarning("多选题正确答案不能少于两项")
            } else if (this.form.answers.find(answer => !answer.val)){
              return this.msgWarning("可选答案内容不能为空")
            }
          } else if (!this.form.difficultyLevel){
            return this.msgWarning("请选择难度等级")
          }
          if (this.form.id) {
            updateStoreQuestion(this.form).then(response => {
              if (response.code === 200) {
                this.open = false;
                this.msgSuccess("修改成功");
                this.reset();
                this.getList();
              }
            });
          } else {
            addStoreQuestion(this.form).then(response => {
              if (response.code === 200) {
                this.open = false;
                this.msgSuccess("新增成功");
                this.reset();
                this.getList();
              }
            });
          }
        }
      });
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除考试试卷题目编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delStoreQuestion(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        }).catch(function() {});
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有考试试卷题目数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportStoreQuestion(queryParams);
        }).then(response => {
          this.download(response.msg);
        }).catch(function() {});
    },

    /** 预览按钮操作 */
    handleView(row) {
      this.questionDetailDialogOpen = true;
      this.form = row;
      this.form.answersView = row.answers ? eval('(' + row.answers + ')') : []
      this.form.correctAnswerView = row.correctAnswer ? eval('(' + row.correctAnswer + ')') : []
      this.referredPapers = []
      getExamQuestionReferredExams(row.id).then(res =>{
        this.referredPapers = res.data
      })
    },

    addNewAnswers(){
      this.form.answers.push({key: String.fromCharCode(65 + this.form.answers.length), val: ''})
    },

    removeNewAnswers(index){
      if(this.form.answers.length < 3){
        return this.msgWarning("不能少于两个选项")
      }
      /* 删除选项 */
      this.form.answers.splice(index, 1)
      /* 重新构建选项键 */
      for (let i=index; i<this.form.answers.length; i++){
        this.form.answers[i].key = String.fromCharCode(65 + i)
      }

      // return
      // let replaceIndex = this.answers.length, currIndex = 0;
      // while (currIndex < this.answers.length - 1){
      //   const answer = this.answers[currIndex]
      //   if (answer.key == key){
      //     replaceIndex = currIndex
      //   }
      //   if (currIndex >= replaceIndex){
      //     this.answers[currIndex] = {
      //       key: String.fromCharCode(65 + currIndex),
      //       val: this.answers[currIndex + 1].val
      //     }
      //   }
      //   currIndex++
      // }
      // this.answers.pop();
    },

    /* 在光标处插入填空标识 */
    insertFillInputContent(){
      const myValue = '${i}'
      const myField = document.getElementById("fill_input_question");
      if (myField.selectionStart || myField.selectionStart === 0) {
        const value = myField.value
        var startPos = myField.selectionStart
        var endPos = myField.selectionEnd
        if (value.substring(startPos - myValue.length, startPos).indexOf(myValue) > -1 || value.substring(endPos, startPos + myValue.length).indexOf(myValue) > -1){
          return this.msgWarning("当前位置已存在输入框，请勿重复插入")
        }
        this.form.question = value.substring(0, startPos) + myValue + value.substring(endPos, value.length)
        this.$nextTick()
        myField.focus()
        myField.setSelectionRange(endPos + myValue.length, endPos + myValue.length)
      } else {
        this.form.question += myValue
      }
    },

    /* 在一个字符串中获取指定子串的数量 */
    getChildCharCount(char, childChar){
      let count = 0
      if (char){
        let pos = char.indexOf(childChar);
        while(pos > -1){
          count++
          pos = char.indexOf(childChar, pos + 1);
        }
      }
      return count
    },

    questionTypeChange(val){
      this.form.question = null
      this.form.answers = []
      this.form.correctAnswer = []
    },

    hoverQuestion(questionKey){
      this.hoverQuestionKey = questionKey
    },

    // 题目考核方向字典翻译
    questionCategoryCodeFormat(row, column) {
      return this.selectDictLabel(this.examModeOptions, row.questionCategoryCode);
    },

    // 试题类型字典翻译
    questionTypeFormat(row, column) {
      return this.selectDictLabel(this.questionTypeOptions, row.questionType);
    },

  }
};
</script>

<style lang="scss" scoped>

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
    border: 1px dashed #88949b;
    border-left: none;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    font-weight: bold;
  }

  .question-content {
    .el-row {
      padding: 0.3rem;
    }
  }

</style>
