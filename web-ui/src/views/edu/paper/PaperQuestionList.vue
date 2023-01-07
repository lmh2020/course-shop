<template>
  <div class="app-container">
    <el-page-header @back="$router.go(-1);" :content="$route.params.paperTitle"/>

    <paper-chart :paperId="paperId" ref="paperStatistics"/>

    <!-- 顶部查询表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px" style="margin-top: 25px;">
      <el-form-item label="考核方向" prop="questionCategoryCode">
        <el-select v-model="queryParams.questionCategoryCode" placeholder="请输入题目考核方向" clearable size="small" style="width: 240px">
          <el-option v-for="mode in examModeOptions" :key="mode.dictValue" :label="mode.dictLabel" :value="mode.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item label="试题类型" prop="questionType">
        <el-select v-model="queryParams.questionType" placeholder="请选择试题类型" clearable size="small">
          <el-option v-for="dict in questionTypeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue"/>
        </el-select>
      </el-form-item>
      <el-form-item label="困难等级" prop="difficultyLevel">
        <el-select v-model="queryParams.difficultyLevel" placeholder="请选择困难等级" clearable size="small">
          <el-option v-for="i in 5" :label="i" :value="i"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="cyan" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 顶部按钮集 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleImportQuestion" v-hasPermi="['exam:question:add']">从题库导入</el-button>
      </el-col>
	  <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 试卷试题表格 -->
    <div id="paperQuestionTable">
      <!-- 第一个表格只是为了在加载数据或数据为空时作为空状态展示 -->
      <el-table v-show="!SINGLE_CHOICE_LIST.length && !MULTIPLE_CHOICE_LIST.length && !JUDGE_LIST.length" v-loading="loading" :data="[]" border>
        <el-table-column label="考核方向" width="150" align="center" prop="questionCategoryCode"/>
        <el-table-column label="试题类型" width="90" align="center" prop="questionType"/>
        <el-table-column show-overflow-tooltip label="题目" align="center" prop="question"/>
        <el-table-column label="分值" width="70" align="center" prop="score" />
        <el-table-column label="通过率" width="80" align="center" prop="totalUsedCount"/>
        <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel"/>
        <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width"/>
      </el-table>
      <el-table data-type="SINGLE_CHOICE_LIST" v-show="SINGLE_CHOICE_LIST && SINGLE_CHOICE_LIST.length" ref="singleChoiceTable" v-loading="loading" :data="SINGLE_CHOICE_LIST" border>
        <el-table-column label="单选题" align="center">
          <el-table-column label="考核方向" width="150" align="center" prop="questionCategoryCode">
            <template slot-scope="scope">
              <el-tag :color="(examModeOptions.find(item => item.dictValue == scope.row.questionCategoryCode) || {}).randomColor" style="color: white;">{{questionCategoryCodeFormat(scope.row)}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="试题类型" width="90" align="center" prop="questionType">
            <template slot-scope="scope">
              <el-tag :color="(questionTypeOptions.find(item => item.dictValue == scope.row.questionType) || {}).randomColor" style="color: white;">{{questionTypeFormat(scope.row.questionType)}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip label="题目" align="center" prop="question">
            <template slot-scope="scope">
              <span v-if="scope.row.questionType == 'FILL_INPUT'">{{scope.row.question.replace(/\${i}/g, "_______")}}</span>
              <span v-else>{{scope.row.question}}</span>
            </template>
          </el-table-column>
          <el-table-column label="分值" width="70" align="center" prop="score" />
          <el-table-column label="通过率" width="80" align="center" prop="totalUsedCount">
            <template slot-scope="scope">
              <el-tooltip placement="top">
                <div slot="content">通过：{{scope.row.totalReachedCount}}次<br/><br/>失败：{{scope.row.totalUsedCount - scope.row.totalReachedCount}}次</div>
                <span>{{scope.row.totalUsedCount > 0 ? Math.floor(scope.row.totalReachedCount * 100 / scope.row.totalUsedCount) + '%' : ''}}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel">
            <template slot-scope="scope">
              <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row, scope.index)">预览</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row.id)" v-hasPermi="['exam:question:remove']" style="margin-left: 20px;">移除</el-button>
              <el-button @mousedown.native="sortBtnHoverId = scope.row.id" @mouseleave.native="sortBtnHoverId = null" @mouseup.native="sortBtnHoverId = null"
                         :type="sortBtnHoverId == scope.row.id ? 'primary' : 'text'" :icon="sortBtnHoverId == scope.row.id ? 'el-icon-sort' : 'el-icon-rank'" class="allowDrag" style="margin-left: 20px;">
                {{sortBtnHoverId == scope.row.id ? '上下拖拽进行排序' : '排序'}}
              </el-button>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
      <el-table data-type="MULTIPLE_CHOICE_LIST" v-show="MULTIPLE_CHOICE_LIST && MULTIPLE_CHOICE_LIST.length" ref="multiChoiceTable" v-loading="loading" :data="MULTIPLE_CHOICE_LIST" border style="margin-top: 20px;">
        <el-table-column label="多选题" align="center">
          <el-table-column label="考核方向" width="150" align="center" prop="questionCategoryCode">
            <template slot-scope="scope">
              <el-tag :color="(examModeOptions.find(item => item.dictValue == scope.row.questionCategoryCode) || {}).randomColor" style="color: white;">{{questionCategoryCodeFormat(scope.row)}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="试题类型" width="90" align="center" prop="questionType">
            <template slot-scope="scope">
              <el-tag :color="(questionTypeOptions.find(item => item.dictValue == scope.row.questionType) || {}).randomColor" style="color: white;">{{questionTypeFormat(scope.row.questionType)}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip label="题目" align="center" prop="question">
            <template slot-scope="scope">
              <span v-if="scope.row.questionType == 'FILL_INPUT'">{{scope.row.question.replace(/\${i}/g, "_______")}}</span>
              <span v-else>{{scope.row.question}}</span>
            </template>
          </el-table-column>
          <el-table-column label="分值" width="70" align="center" prop="score" />
          <el-table-column label="通过率" width="80" align="center" prop="totalUsedCount">
            <template slot-scope="scope">
              <el-tooltip placement="top">
                <div slot="content">通过：{{scope.row.totalReachedCount}}次<br/><br/>失败：{{scope.row.totalUsedCount - scope.row.totalReachedCount}}次</div>
                <span>{{scope.row.totalUsedCount > 0 ? Math.floor(scope.row.totalReachedCount * 100 / scope.row.totalUsedCount) + '%' : ''}}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel">
            <template slot-scope="scope">
              <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row, scope.index)">预览</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row.id)" v-hasPermi="['exam:question:remove']" style="margin-left: 20px;">移除</el-button>
              <el-button @mousedown.native="sortBtnHoverId = scope.row.id" @mouseleave.native="sortBtnHoverId = null" @mouseup.native="sortBtnHoverId = null"
                         :type="sortBtnHoverId == scope.row.id ? 'primary' : 'text'" :icon="sortBtnHoverId == scope.row.id ? 'el-icon-sort' : 'el-icon-rank'" class="allowDrag" style="margin-left: 20px;">
                {{sortBtnHoverId == scope.row.id ? '上下拖拽进行排序' : '排序'}}
              </el-button>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
      <el-table data-type="JUDGE_LIST" v-show="JUDGE_LIST && JUDGE_LIST.length" ref="judgeTable" v-loading="loading" :data="JUDGE_LIST" border style="margin-top: 20px;">
        <el-table-column label="判断题" align="center">
          <el-table-column label="考核方向" width="150" align="center" prop="questionCategoryCode">
            <template slot-scope="scope">
              <el-tag :color="(examModeOptions.find(item => item.dictValue == scope.row.questionCategoryCode) || {}).randomColor" style="color: white;">{{questionCategoryCodeFormat(scope.row)}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="试题类型" width="90" align="center" prop="questionType">
            <template slot-scope="scope">
              <el-tag :color="(questionTypeOptions.find(item => item.dictValue == scope.row.questionType) || {}).randomColor" style="color: white;">{{questionTypeFormat(scope.row.questionType)}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip label="题目" align="center" prop="question">
            <template slot-scope="scope">
              <span v-if="scope.row.questionType == 'FILL_INPUT'">{{scope.row.question.replace(/\${i}/g, "_______")}}</span>
              <span v-else>{{scope.row.question}}</span>
            </template>
          </el-table-column>
          <el-table-column label="分值" width="70" align="center" prop="score" />
          <el-table-column label="通过率" width="80" align="center" prop="totalUsedCount">
            <template slot-scope="scope">
              <el-tooltip placement="top">
                <div slot="content">通过：{{scope.row.totalReachedCount}}次<br/><br/>失败：{{scope.row.totalUsedCount - scope.row.totalReachedCount}}次</div>
                <span>{{scope.row.totalUsedCount > 0 ? Math.floor(scope.row.totalReachedCount * 100 / scope.row.totalUsedCount) + '%' : ''}}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel">
            <template slot-scope="scope">
              <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row, scope.index)">预览</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row.id)" v-hasPermi="['exam:question:remove']" style="margin-left: 20px;">移除</el-button>
              <el-button @mousedown.native="sortBtnHoverId = scope.row.id" @mouseleave.native="sortBtnHoverId = null" @mouseup.native="sortBtnHoverId = null"
                         :type="sortBtnHoverId == scope.row.id ? 'primary' : 'text'" :icon="sortBtnHoverId == scope.row.id ? 'el-icon-sort' : 'el-icon-rank'" class="allowDrag" style="margin-left: 20px;">
                {{sortBtnHoverId == scope.row.id ? '上下拖拽进行排序' : '排序'}}
              </el-button>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
    </div>

    <!-- 题库弹窗 -->
    <el-dialog title="题库" @close="storeQuestionDialogClose" :visible.sync="questionStoreOpen" width="90%" append-to-body>
      <div class="app-container">

        <!-- 表格搜索栏 -->
        <el-form :model="questionQueryParams" ref="questionQueryForm" :inline="true" label-width="68px">
          <el-form-item label="考核方向" prop="questionCategoryCodeArr">
            <el-select v-model="questionCategoryCodeArr" placeholder="请选择考核方向" multiple clearable size="small">
              <el-option v-for="mode in examModeOptions" :key="mode.dictValue" :label="mode.dictLabel" :value="mode.dictValue" />
            </el-select>
          </el-form-item>
          <el-form-item label="试题类型" prop="questionType">
            <el-select v-model="questionQueryParams.questionType" placeholder="请选择试题类型" clearable size="small">
              <el-option v-for="questionType in questionTypeOptions" :key="questionType.dictValue" :label="questionType.dictLabel" :value="questionType.dictValue" />
            </el-select>
          </el-form-item>
          <el-form-item label="添加状态" prop="questionType">
            <el-select v-model="questionQueryParams.added" placeholder="请选择是否被添加" clearable size="small">
              <el-option label="全部" value="" />
              <el-option label="已添加" value="Y" />
              <el-option label="未添加" value="N" />
            </el-select>
          </el-form-item>
          <el-form-item label="题目" prop="question">
            <el-input v-model="questionQueryParams.question" placeholder="请输入题目" clearable size="small"/>
          </el-form-item>
          <el-form-item label="困难等级" prop="question">
            <el-select v-model="questionQueryParams.difficultyLevel" placeholder="请选择困难等级" clearable size="small">
              <el-option v-for="i in 5" :label="i" :value="i"/>
            </el-select>
          </el-form-item>
          <el-form-item label="分值" prop="score">
            <el-input-number v-model="questionQueryParams.score" :min="1" :max="50" label="请输入分值"/>
          </el-form-item>
          <el-form-item>
            <el-button type="cyan" icon="el-icon-search" size="mini" @click="questionQueryParams.pageNum = 1;getQuestionStoreList();">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click='resetForm("questionQueryForm");questionQueryParams.pageNum = 1;getQuestionStoreList();'>重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 表格按钮 -->
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-thumb" :disabled="checkedStoreQuestionIds ? false : true" @click="importQuestionIntoPaper(null)">批量提交</el-button>
          </el-col>
        </el-row>

        <!-- 表格 -->
        <el-table v-loading="questionLoading" :data="storeQuestionList" border @selection-change="handleStoreQuestionSelectionChange">
          <el-table-column :selectable="(row, index) => {return row.added == 'Y' ? 0 : 1}" type="selection" width="60" align="center" />
          <el-table-column label="考核方向" width="150" align="center" prop="questionCategoryCode">
            <template slot-scope="scope">
              <el-tag :color="(examModeOptions.find(item => item.dictValue == scope.row.questionCategoryCode) || {}).randomColor" style="color: white;">{{questionCategoryCodeFormat(scope.row)}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="试题类型" width="90" align="center" prop="questionType">
            <template slot-scope="scope">
              <el-tag :color="(questionTypeOptions.find(item => item.dictValue == scope.row.questionType) || {}).randomColor" style="color: white;">{{questionTypeFormat(scope.row.questionType)}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip label="题目" align="center" prop="question">
            <template slot-scope="scope">
              <span v-if="scope.row.questionType == 'FILL_INPUT'">{{scope.row.question.replace(/\${i}/g, "_______")}}</span>
              <span v-else>{{scope.row.question}}</span>
            </template>
          </el-table-column>
          <el-table-column label="分值" width="70" align="center" prop="score" />
          <el-table-column label="通过率" width="80" align="center" prop="totalUsedCount">
            <template slot-scope="scope">
              <el-tooltip placement="top">
                <div slot="content">通过：{{scope.row.totalReachedCount}}次<br/><br/>失败：{{scope.row.totalUsedCount - scope.row.totalReachedCount}}次</div>
                <span>{{scope.row.totalUsedCount > 0 ? Math.floor(scope.row.totalReachedCount * 100 / scope.row.totalUsedCount) + '%' : ''}}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel">
            <template slot-scope="scope">
              <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
          <el-table-column label="添加状态" width="170" align="center" prop="added">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.added === 'Y'" type="info" effect="dark">已添加</el-tag>
              <el-tag v-if="scope.row.added === 'N'" type="success" effect="dark">未添加</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button @click="handleView(scope.row, scope.index)" icon="el-icon-view">预览</el-button>
              <el-button @click="importQuestionIntoPaper(scope.row.id)" :disabled="scope.row.added === 'Y'" icon="el-icon-thumb" type="primary">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="questionTotal > 0" :total="questionTotal" :page.sync="questionQueryParams.pageNum" :limit.sync="questionQueryParams.pageSize" @pagination="getQuestionStoreList"/>
      </div>
    </el-dialog>

    <!-- 预览详细 -->
    <el-dialog title="试题详情" :visible.sync="questionDialogOpen" width="60%" append-to-body :destroy-on-close="true">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <el-row :gutter="20">
            <el-col :span="16"><div><i class="el-icon-question"/> 问题：
              <span>{{form.question}}</span>
            </div></el-col>
            <el-col :span="8">
              <span style="float: right; margin-left: 20px;"><el-rate :value="form.difficultyLevel" disabled/></span>
              <div class="questio-bar"><span class="q-count">分数</span><span class="q-score">{{form.score}}</span></div>
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
        <el-tag v-for="paper in referredExams" effect="dark" style="margin-left: 10px;">{{ paper.title }}</el-tag>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="questionDialogOpen = false;referredExams = [];form = {};">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {getExamQuestionReferredExams} from "@/api/edu/questionStore";
import {getPaperQuestionList, importQuestion, removeStoreQuestion, storeQuestionListForPaper, changePaperQuestionSort} from "@/api/edu/paper";
import PaperChart from '@/views/components/chart/PaperChart'
import Sortable from 'sortablejs'

export default {
  name: "Question",
  components: {
    PaperChart,
    Sortable
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: null,
      // 显示搜索条件
      showSearch: true,
      sortBtnHoverId: null,
      // 查询参数
      queryParams: {
        // pageNum: 1,
        // pageSize: 10,
        paperId: this.$route.params.paperId,
        questionCategoryCode: null,
        question: null,
        questionType: null,
        difficultyLevel: null
      },
      // 表单参数
      form: {},
      questionList: [],
      SINGLE_CHOICE_LIST: [],
      MULTIPLE_CHOICE_LIST: [],
      JUDGE_LIST: [],
      // 表单校验
      rules: {
        questionCategoryCode: [
          { required: true, message: "题目考核方向不能为空", trigger: "blur" }
        ],
        question: [
          { required: true, message: "题目不能为空", trigger: "blur" }
        ],
        questionType: [
          { required: true, message: "试题类型不能为空", trigger: "change" }
        ],
        difficultyLevel: [
          { required: true, message: "困难等级不能为空", trigger: "blur" }
        ],
        score: [
          { required: true, message: "分值不能为空", trigger: "blur" }
        ],
      },
      /* ------------------------------------ 下面是题库弹窗的表格变量 ------------------------------------- */
      paperId: this.$route.params.paperId,
      //题库弹窗
      questionStoreOpen: false,
      questionDialogOpen: false,
      referredExams: [],
      /* 已选中的题库试题ID */
      checkedStoreQuestionIds: null,
      // 考试试卷题目表格数据
      storeQuestionList: [],
      // 查询参数
      questionQueryParams: {
        pageNum: 1,
        pageSize: 10,
        paperId: this.$route.params.paperId,
        added: 'N',
        questionCategoryCodes: null,
        question: null,
        questionType: null,
        score: undefined
      },
      // 题目类型字典
      questionTypeOptions: [],
      // 考核方向数据字典
      examModeOptions: [],
      questionCategoryCodeArr: [],
      questionLoading: true,
      // 总条数
      questionTotal: 0,
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

    /* 表格行拖拽调换顺序 */
    rowDrop() {
      const _this = this
      document.querySelectorAll('#paperQuestionTable .el-table__body-wrapper table tbody').forEach(tbody => {
        Sortable.create(tbody, {
          // handle: ".allowDrag",
          animation: 150,
          onEnd(evt) {
            if (evt.oldIndex == evt.newIndex){
              return
            }
            const _tableDom = evt.path.find(pathItem => pathItem.classList.length && pathItem.classList.contains('el-table'))
            const dataName = _tableDom.dataset.type
            if (!dataName){
              return _this.msgWarning("操作失败");
            }
            _this.loading = true
            changePaperQuestionSort({
              paperId: _this.paperId,
              fromId: _this[dataName][evt.oldIndex].id,
              toId: _this[dataName][evt.newIndex].id,
              opt: evt.oldIndex > evt.newIndex ? "UP" : "DOWM"
            }).then(res =>{
              if (res.code === 200) {
                _this.msgSuccess("操作成功");
                _this.resetQuery()
              }
              _this.loading = false
            })
          }
        })
      })
    },

    /** 查询考试试卷题目列表 */
    getList() {
      this.loading = true;
      this.SINGLE_CHOICE_LIST = []
      this.MULTIPLE_CHOICE_LIST = []
      this.JUDGE_LIST = []
      getPaperQuestionList(this.queryParams).then(({data}) => {
        if (!data){
          data = {}
        }
        this.SINGLE_CHOICE_LIST = this.getQuestionByType(data,'SINGLE_CHOICE')
        this.MULTIPLE_CHOICE_LIST = this.getQuestionByType(data,'MULTIPLE_CHOICE')
        this.JUDGE_LIST = this.getQuestionByType(data,'JUDGE')
        this.loading = false;
        this.rowDrop()
      });
    },

    getQuestionByType(data, questionType){
      var dataList = data[questionType]
      if (dataList && dataList.length){
        dataList.forEach(data => data.questionType = data.questionType.code)
      }
      return dataList || []
    },

    /** 查询考试试卷题目列表 */
    getQuestionStoreList() {
      this.storeQuestionList = []
      this.questionLoading = true;
      if (this.questionCategoryCodeArr && this.questionCategoryCodeArr.length){
        this.questionQueryParams.questionCategoryCodes = this.questionCategoryCodeArr.join(",")
      } else {
        this.questionQueryParams.questionCategoryCodes = null
      }
      storeQuestionListForPaper(this.questionQueryParams).then(response => {
        if (response.rows && response.rows.length){
          this.storeQuestionList = response.rows;
          for(let question of this.storeQuestionList){
            question.questionType = question.questionType.code
          }
        }
        this.questionTotal = response.total;
        this.questionLoading = false;
      });
    },
    // 题目考核方向字典翻译
    questionCategoryCodeFormat(row, column) {
      return this.selectDictLabel(this.examModeOptions, row.questionCategoryCode);
    },
    // 题目类型字典翻译
    questionTypeFormat(questionType) {
      return this.selectDictLabel(this.questionTypeOptions, questionType);
    },

    // 表单重置
    reset() {
      this.form = {
        id: null,
        questionCategoryCode: null,
        question: null,
        questionType: null,
        answers: null,
        correctAnswer: null,
        analysis: null,
        difficultyLevel: null,
        totalUsedCount: null,
        totalReachedCount: null,
        score: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        version: null,
        delFlag: null
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
      this.ids = selection.map(item => item.id).join(",")
    },

    /** 新增按钮操作 */
    handleImportQuestion() {
      this.getQuestionStoreList()
      this.questionStoreOpen = true;
    },

    /** 删除按钮操作 */
    handleDelete(id) {
      const paperId = this.paperId
      this.$confirm('是否确认从当前试卷中移除该试题数据项?', "警告", {
          confirmButtonText: "移除",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return removeStoreQuestion({paperId: paperId, storeQuestionId: id});
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");

          /* 重新刷新Echarts统计子组件 */
          this.$refs.paperStatistics.initChart(['questionPart'])
        }).catch(function() {});
    },

    /* --------------------------------------- 下面是题库部分的操作 --------------------------------------- */
    /** 预览按钮操作 */
    handleView(row) {
      this.questionDialogOpen = true;
      this.form = row;
      this.form.answersView = row.answers ? eval('(' + row.answers + ')') : []
      this.form.correctAnswerView = row.correctAnswer ? eval('(' + row.correctAnswer + ')') : []
      this.referredExams = []
      getExamQuestionReferredExams(row.id).then(res =>{
        this.referredExams = res.data
      })
    },

    // 题库试题多选框选中数据
    handleStoreQuestionSelectionChange(selection) {
      this.checkedStoreQuestionIds = selection.map(item => item.id).join(",")
    },

    /* 选择试题导入到试卷 */
    importQuestionIntoPaper(storeQuestionId){
      importQuestion({
        paperId: this.paperId,
        storeQuestionIds: storeQuestionId || this.checkedStoreQuestionIds
      }).then(res =>{
        if (res.code === 200) {
          this.msgSuccess("添加成功");
          this.questionQueryParams.pageNum = 1;
          this.getQuestionStoreList();
          this.resetQuery()
          /* 重新刷新Echarts统计子组件 */
          this.$refs.paperStatistics.initChart(['questionPart'])
        }
      })
    },

    /* 题库弹窗关闭回调 */
    storeQuestionDialogClose(){
      /* 已选中的题库试题ID */
      this.checkedStoreQuestionIds = null
      // 考试试卷题目表格数据
      this.storeQuestionList = []
      this.questionCategoryCodeArr = []
      // 查询参数
      this.questionQueryParams = {
        pageNum: 1,
        pageSize: 10,
        paperId: this.$route.params.paperId,
        added: 'N',
        questionCategoryCodes: null,
        question: null,
        questionType: null,
        score: undefined
      }
    }

  }
};
</script>


<style lang="scss" scoped>

  .questio-bar {
    position: relative;
    float: right;
  }

  .questio-bar .q-count {
    margin-right: 60px;
    color: #fff;
    padding: 4px 10px;
    background-color: #ff6767;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
    border: 1px solid #ff6767;
  }
  .questio-bar .q-score {
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
