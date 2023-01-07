<template>
  <div class="app-container">
    <el-page-header @back="$router.go(-1);" :content="$route.params.examTitle"/>

    <!-- 顶部查询表单 -->
    <el-form :model="examQuestionQueryParams" ref="examQuestionQueryForm" :inline="true" label-width="68px" style="margin-top: 25px;">
      <el-form-item label="考核方向" prop="questionCategoryCode">
        <el-select v-model="examQuestionQueryParams.questionCategoryCode" placeholder="请输入题目考核方向" clearable size="small" style="width: 240px">
          <el-option v-for="mode in examModeOptions" :key="mode.dictValue" :label="mode.dictLabel" :value="mode.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item label="试题类型" prop="questionType">
        <el-select v-model="examQuestionQueryParams.questionType" placeholder="请选择试题类型" clearable size="small">
          <el-option v-for="dict in questionTypeOptions" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue"/>
        </el-select>
      </el-form-item>
      <el-form-item label="困难等级" prop="difficultyLevel">
        <el-select v-model="examQuestionQueryParams.difficultyLevel" placeholder="请选择困难等级" clearable size="small">
          <el-option v-for="i in 5" :label="i" :value="i"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="cyan" icon="el-icon-search" size="mini" @click="examQuestionQueryParams.pageNum = 1;getExamQuestionList();">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetForm('examQuestionQueryForm');examQuestionQueryParams.pageNum = 1;getExamQuestionList();">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 顶部按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button @click="importFromPaperStore" type="primary" icon="el-icon-plus" size="mini">从试卷池导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button @click="importFromQuestionStore" type="success" icon="el-icon-plus" size="mini">从题库导入</el-button>
      </el-col>
    </el-row>

    <!-- 已添加的试题表格 -->
    <div id="paperQuestionTable">
      <!-- 第一个表格只是为了在加载数据或数据为空时作为空状态展示 -->
      <el-table v-show="!SINGLE_CHOICE_LIST.length && !MULTIPLE_CHOICE_LIST.length && !JUDGE_LIST.length" v-loading="examQuestionTableLoading" :data="[]" border>
        <el-table-column label="考核方向" width="150" align="center" prop="questionCategoryCode"/>
        <el-table-column label="试题类型" width="90" align="center" prop="questionType"/>
        <el-table-column show-overflow-tooltip label="题目" align="center" prop="question"/>
        <el-table-column label="分值" width="70" align="center" prop="score" />
        <el-table-column label="通过率" width="80" align="center" prop="totalUsedCount"/>
        <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel"/>
        <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width"/>
      </el-table>
      <el-table data-type="SINGLE_CHOICE_LIST" v-show="SINGLE_CHOICE_LIST && SINGLE_CHOICE_LIST.length" ref="singleChoiceTable" v-loading="examQuestionTableLoading" :data="SINGLE_CHOICE_LIST" border>
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
          <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel">
            <template slot-scope="scope">
              <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-view" @click="handleQuestionView(scope.row)">预览</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleExamQuestionDelete(scope.row.id)" v-hasPermi="['exam:question:remove']" style="margin-left: 20px;">移除</el-button>
              <el-button @mousedown.native="sortBtnHoverId = scope.row.id" @mouseleave.native="sortBtnHoverId = null" @mouseup.native="sortBtnHoverId = null"
                         :type="sortBtnHoverId == scope.row.id ? 'primary' : 'text'" :icon="sortBtnHoverId == scope.row.id ? 'el-icon-sort' : 'el-icon-rank'" class="allowDrag" style="margin-left: 20px;">
                {{sortBtnHoverId == scope.row.id ? '上下拖拽进行排序' : '排序'}}
              </el-button>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
      <el-table data-type="MULTIPLE_CHOICE_LIST" v-show="MULTIPLE_CHOICE_LIST && MULTIPLE_CHOICE_LIST.length" ref="multiChoiceTable" v-loading="examQuestionTableLoading" :data="MULTIPLE_CHOICE_LIST" border style="margin-top: 20px;">
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
          <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel">
            <template slot-scope="scope">
              <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-view" @click="handleQuestionView(scope.row, scope.index)">预览</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleExamQuestionDelete(scope.row.id)" v-hasPermi="['exam:question:remove']" style="margin-left: 20px;">移除</el-button>
              <el-button @mousedown.native="sortBtnHoverId = scope.row.id" @mouseleave.native="sortBtnHoverId = null" @mouseup.native="sortBtnHoverId = null"
                         :type="sortBtnHoverId == scope.row.id ? 'primary' : 'text'" :icon="sortBtnHoverId == scope.row.id ? 'el-icon-sort' : 'el-icon-rank'" class="allowDrag" style="margin-left: 20px;">
                {{sortBtnHoverId == scope.row.id ? '上下拖拽进行排序' : '排序'}}
              </el-button>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
      <el-table data-type="JUDGE_LIST" v-show="JUDGE_LIST && JUDGE_LIST.length" ref="judgeTable" v-loading="examQuestionTableLoading" :data="JUDGE_LIST" border style="margin-top: 20px;">
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
          <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel">
            <template slot-scope="scope">
              <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="170" align="center" prop="createTime" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-view" @click="handleQuestionView(scope.row, scope.index)">预览</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleExamQuestionDelete(scope.row.id)" v-hasPermi="['exam:question:remove']" style="margin-left: 20px;">移除</el-button>
              <el-button @mousedown.native="sortBtnHoverId = scope.row.id" @mouseleave.native="sortBtnHoverId = null" @mouseup.native="sortBtnHoverId = null"
                         :type="sortBtnHoverId == scope.row.id ? 'primary' : 'text'" :icon="sortBtnHoverId == scope.row.id ? 'el-icon-sort' : 'el-icon-rank'" class="allowDrag" style="margin-left: 20px;">
                {{sortBtnHoverId == scope.row.id ? '上下拖拽进行排序' : '排序'}}
              </el-button>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="从试卷池导入试题" :visible.sync="paperStoreDialogOpen" @close="PaperStoreDialogClose" width="80%" append-to-body>
      <el-form :model="paperStoreQueryParams" ref="paperStoreQueryForm" :inline="true" label-width="68px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="paperStoreQueryParams.title" placeholder="请输入标题" clearable size="small" @keyup.enter.native="handlePaperStoreQuery"/>
        </el-form-item>
        <el-form-item>
          <el-button type="cyan" icon="el-icon-search" size="mini" @click="handlePaperStoreQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetForm('paperStoreQueryForm');handlePaperStoreQuery;">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="paperStoreTableLoading" :data="paperStoreList" border>
        <el-table-column label="标题" align="center" prop="title">
          <template slot-scope="scope">
            <el-tooltip class="item" effect="dark" content="点击查看该试卷" placement="left">
              <router-link target="_blank" :to="'/exam/paper/question/' + scope.row.id + '/' + scope.row.title" class="link-type">
                <span>{{ scope.row.title }}</span>
              </router-link>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="满分" width="100" align="center" prop="totalScore" />
        <el-table-column show-overflow-tooltip label="试卷说明" align="center" prop="detail" />
        <el-table-column label="困难等级" width="170" align="center" prop="difficultyLevel">
          <template slot-scope="scope">
            <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button @click="openPaperQuestionExportDialog(scope.row)" slot="reference" icon="el-icon-thumb" type="primary">导入该试卷</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="paperStoreTotal" :total="paperStoreTotal" :page.sync="paperStoreQueryParams.pageNum" :limit.sync="paperStoreQueryParams.pageSize" @pagination="getPaperStoreList"/>
    </el-dialog>

    <el-dialog title="从题库导入试题" @close="storeQuestionDialogClose" :visible.sync="questionStoreDialogOpen" width="90%" append-to-body>
      <el-form :model="questionStoreQueryParams" ref="questionStoreQueryForm" :inline="true" label-width="68px">
        <el-form-item label="考核方向" prop="questionCategoryCodeArr">
          <el-select v-model="questionCategoryCodeArr" placeholder="请选择考核方向" multiple clearable size="small">
            <el-option v-for="mode in examModeOptions" :key="mode.dictValue" :label="mode.dictLabel" :value="mode.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="试题类型" prop="questionType">
          <el-select v-model="questionStoreQueryParams.questionType" placeholder="请选择试题类型" clearable size="small">
            <el-option v-for="questionType in questionTypeOptions" :key="questionType.dictValue" :label="questionType.dictLabel" :value="questionType.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="添加状态" prop="questionType">
          <el-select v-model="questionStoreQueryParams.added" placeholder="请选择是否被添加" clearable size="small">
            <el-option label="全部" value="" />
            <el-option label="已添加" value="Y" />
            <el-option label="未添加" value="N" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目" prop="question">
          <el-input v-model="questionStoreQueryParams.question" placeholder="请输入题目" clearable size="small"/>
        </el-form-item>
        <el-form-item label="困难等级" prop="question">
          <el-select v-model="questionStoreQueryParams.difficultyLevel" placeholder="请选择困难等级" clearable size="small">
            <el-option v-for="i in 5" :label="i" :value="i"/>
          </el-select>
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="questionStoreQueryParams.score" :min="1" :max="50" label="请输入分值"/>
        </el-form-item>
        <el-form-item>
          <el-button type="cyan" icon="el-icon-search" size="mini" @click="questionStoreQueryParams.pageNum = 1;getQuestionStoreList();">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click='resetForm("questionStoreQueryForm");questionStoreQueryParams.pageNum = 1;getQuestionStoreList();'>重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button @click="importByQuestion(null)" type="primary" icon="el-icon-thumb" :disabled="checkedStoreQuestionIds ? false : true">批量导入</el-button>
        </el-col>
      </el-row>

      <!-- 题库表格 -->
      <el-table v-loading="questionStoreLoading" :data="storeQuestionList" border @selection-change="handleStoreQuestionSelectionChange">
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
            <el-button @click="handleQuestionView(scope.row, scope.index)" icon="el-icon-view">预览</el-button>
            <el-popconfirm @onConfirm="importByQuestion(scope.row.id)" title="确定添加该试题到考核中么？" style="margin-left: 20px;">
              <el-button :disabled="scope.row.added === 'Y'" slot="reference" icon="el-icon-thumb" type="primary">选择</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="storeQuestionTotal" :total="storeQuestionTotal" :page.sync="questionStoreQueryParams.pageNum" :limit.sync="questionStoreQueryParams.pageSize" @pagination="getQuestionStoreList"/>
    </el-dialog>

    <!-- 试题预览详细 -->
    <el-dialog title="试题详情" :visible.sync="questionViewDialogOpen" width="60%" append-to-body :destroy-on-close="true">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <el-row :gutter="20">
            <el-col :span="16"><div><i class="el-icon-question"/> 问题：
              <span>{{storeQuestionModel.question}}</span>
            </div></el-col>
            <el-col :span="8">
              <span style="float: right; margin-left: 20px;"><el-rate :value="storeQuestionModel.difficultyLevel" disabled/></span>
              <div class="questio-bar"><span class="q-count">分数</span><span class="q-score">{{storeQuestionModel.score}}</span></div>
            </el-col>
          </el-row>
        </div>
        <div class="question-content">
          <el-row v-for="answer in storeQuestionModel.answers">
            <el-tag v-if="storeQuestionModel.correctAnswer.indexOf(answer.key) > -1" type="success" effect="dark">{{answer.key}}</el-tag>
            <el-tag v-else type="info">{{answer.key}}</el-tag>
            <span style="margin-left: 15px;">{{answer.val}}</span>
          </el-row>
        </div>
      </el-card>
      <el-card class="box-card" style="margin-top: 20px;">
        <div slot="header" class="clearfix"><i class="el-icon-s-opportunity"/><span> 答案解析</span></div>
        <div>{{storeQuestionModel.analysis}}</div>
      </el-card>
      <div>
        <el-divider content-position="left"><i class="el-icon-s-flag"></i> 被以下试卷引用</el-divider>
        <el-tag v-for="paper in storeQuestionReferredExams" effect="dark" style="margin-left: 10px;">{{ paper.title }}</el-tag>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="questionViewDialogOpen = false;storeQuestionReferredExams = [];storeQuestionModel = {};">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 从试卷导出试题到考核弹窗 -->
    <el-dialog title="从试卷导入" :visible.sync="paperQuestionExportDialogOpen" width="60%" @close="paperQuestionExportDialogClose()" append-to-body :close-on-click-modal="false" :destroy-on-close="true">
      <el-steps :active="paperQuestionExportActiveStep" align-center>
        <el-step title="试卷信息" icon="el-icon-s-data" description="（请在导入前确认试卷信息）"></el-step>
        <el-step title="导入须知" icon="el-icon-upload"></el-step>
        <el-step title="导入结果" icon="el-icon-info"></el-step>
      </el-steps>

      <div v-if="paperQuestionExportActiveStep == 1">
        <el-card class="box-card" style="margin: 25px;">
          <div slot="header" class="clearfix">
            <span v-text="selectedPaperInfo.title"></span>
            <el-tooltip class="item" effect="dark" content="点击查看该试卷" placement="left">
              <router-link target="_blank" :to="'/exam/paper/question/' + selectedPaperInfo.id + '/' + selectedPaperInfo.title" class="link-type" style="float: right; padding: 3px 0">
                <span>查看试卷详情</span>
              </router-link>
            </el-tooltip>
          </div>
          <paper-chart v-if="paperQuestionExportDialogOpen" :paperId="selectedPaperInfo.id" :searchMode="['questionPart']"/>
        </el-card>
        <el-form label-width="100px">
          <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
            <el-button type="primary" @click="paperQuestionExportActiveStep = 2">下一步</el-button>
            <el-button @click="paperQuestionExportDialogClose()">关闭</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="paperQuestionExportActiveStep == 2">
        <el-card class="box-card" style="margin: 25px;">
          <el-alert title="提示" type="info" description="导入持续时长会取决于该试卷试题总数，请耐心等待导出结果" show-icon :closable="false"/>
          <el-alert title="提示" type="info" description="若导入过程中发生错误，请根据提示进行下一步操作" show-icon :closable="false" style="margin-top: 12px;"/>
          <el-alert title="提示" type="info" description="试题导入后，考核试题将作为副本独立存储，试题库与试卷库的任何编辑都不会影响该考核试卷" show-icon :closable="false" style="margin-top: 12px;"/>
          <el-alert title="警告" type="warning" description="为了避免影响考核结果数据，该考核进行的前后一段时间内，均无法再对其进行导出、编辑等操作，请注意提前检查考核试卷" show-icon :closable="false" style="margin-top: 12px;"/>
        </el-card>
        <el-form label-width="100px">
          <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
            <el-button type="primary" @click="importByPaper()">开始导入</el-button>
            <el-button @click="paperQuestionExportDialogClose()">关闭</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="paperQuestionExportActiveStep == 3">
        <el-card class="box-card" style="margin: 25px;overflow: visible;">
          <div slot="header" class="clearfix">
            <SlideProcess :process="exportProcess" :status="exportStatus" style="margin-top: 20px;"/>
          </div>
          <div class="block" style="width: 600px;">
            <el-timeline>
              <transition-group name="el-fade-in-linear">
                <el-timeline-item v-for="(processItems, processCode) in importByPaperQuestionProcessTimeLine" :key="processCode" :timestamp="processItems[0].time"
                                  :type="processItems[processItems.length - 1].type" :icon="processItems[processItems.length - 1].icon" size="large" placement="top">
                  <div class="lay-container">
                    <el-card class="lay-item" v-for="item in processItems" :class="{'lay-success': item.type == 'primary', 'lay-warning': item.type == 'warning', 'lay-fail': item.type == 'danger'}">
                      <h4 style="margin-top: 0px;">{{item.title}}</h4>
                      <div>{{item.content}}</div>
                    </el-card>
                  </div>
                </el-timeline-item>
              </transition-group>
            </el-timeline>
          </div>
        </el-card>
      </div>
    </el-dialog>
  </div>

</template>
<script>
import Sortable from 'sortablejs'
import {
  listPaper,
} from "@/api/edu/paper";
import {examQuestionList, storeQuestionListForExam} from "@/api/edu/exam";
import {importByQuestionIds, importByPaperQuestion, delQuestion, changeExamQuestionSort} from "@/api/edu/examQuestion.js";
import {getExamQuestionReferredExams} from "@/api/edu/questionStore";
import PaperChart from '@/views/components/chart/PaperChart'
import SlideProcess from '@/views/components/animation/SlideProcess'

export default {
  name: "examQuestion",
  components: {Sortable, PaperChart, SlideProcess},
  data() {
    return {
      examId: this.$route.params.examId,
      examTitle: this.$route.params.examTitle,
      // 考核方向数据字典
      examModeOptions: [],
      // 试题类型字典
      questionTypeOptions: [],
      examQuestionTableLoading: false,
      // 表格的高度
      tableHeight: document.documentElement.scrollHeight - 245 + "px",
      examQuestionQueryParams: {
        examId: this.$route.params.examId,
        questionCategoryCode: null,
        question: null,
        questionType: null,
        difficultyLevel: null
      },
      sortBtnHoverId: null,
      // 表列信息
      examQuestionList: [],
      // 字典信息
      dictOptions: [],

      /*##############*/
      SINGLE_CHOICE_LIST: [],
      MULTIPLE_CHOICE_LIST: [],
      JUDGE_LIST: [],

/* #################下面是【试卷】资源池变量################# */
      paperStoreDialogOpen: false,
      paperQuestionExportDialogOpen: false,
      questionStoreLoading: false,
      paperQuestionExportActiveStep: 1,
      exportProcess: 0,
      exportStatus: null,
      importByPaperQuestionProcessTimeLine: {

      },
      paperStoreQueryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        difficultyLevel: null,
      },
      paperStoreTableLoading: false,
      paperStoreTotal: 0,
      paperStoreList: [],

      selectedPaperInfo: {},

/* #################下面是【题库】变量################# */
      questionStoreDialogOpen: false,
      questionViewDialogOpen: false,
      /* 已选中的题库试题ID */
      checkedStoreQuestionIds: null,
      // 考试试卷题目表格数据
      storeQuestionList: [],
      // 总条数
      storeQuestionTotal: 0,
      questionCategoryCodeArr: [],
      questionStoreQueryParams: {
        pageNum: 1,
        pageSize: 10,
        examId: this.$route.params.examId,
        added: 'N',
        questionCategoryCodes: null,
        question: null,
        questionType: null,
        score: undefined
      },
      storeQuestionModel: {},
      storeQuestionReferredExams: [],
    };
  },
  created() {
    this.getMultiDicts(["edu_exam_mode", "edu_exam_question_type"]).then((response) => {
      const multiDicts = response.data
      this.examModeOptions = multiDicts.edu_exam_mode;
      this.fillRandomColors(this.examModeOptions)
      this.questionTypeOptions = multiDicts.edu_exam_question_type;
      this.fillRandomColors(this.questionTypeOptions)
    });
    this.getExamQuestionList()
  },
  mounted() {

  },
  methods: {

    /** 查询考试试卷题目列表 */
    getExamQuestionList() {
      this.examQuestionTableLoading = true;
      this.SINGLE_CHOICE_LIST = []
      this.MULTIPLE_CHOICE_LIST = []
      this.JUDGE_LIST = []
      examQuestionList(this.examQuestionQueryParams).then(({data}) => {
        if (!data){
          data = {}
        }
        this.SINGLE_CHOICE_LIST = this.getQuestionByType(data,'SINGLE_CHOICE')
        this.MULTIPLE_CHOICE_LIST = this.getQuestionByType(data,'MULTIPLE_CHOICE')
        this.JUDGE_LIST = this.getQuestionByType(data,'JUDGE')
        this.examQuestionTableLoading = false;
        this.rowDrop()
      });
    },

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
            _this.examQuestionTableLoading = true
            changeExamQuestionSort({
              fromId: _this[dataName][evt.oldIndex].id,
              toId: _this[dataName][evt.newIndex].id,
              opt: evt.oldIndex > evt.newIndex ? "UP" : "DOWM"
            }).then(res =>{
              if (res.code === 200) {
                _this.resetForm('examQuestionQueryForm')
                _this.examQuestionQueryParams.pageNum = 1
                _this.getExamQuestionList();
                _this.msgSuccess("操作成功");
              }
              _this.examQuestionTableLoading = false
            })
          }
        })
      })
    },

    getQuestionByType(data, questionType){
      var dataList = data[questionType]
      if (dataList && dataList.length){
        dataList.forEach(data => data.questionType = data.questionType.code)
      }
      return dataList || []
    },

    /** 删除按钮操作 */
    handleExamQuestionDelete(id) {
      this.$confirm('是否确认从当前考核中移除该试题数据项?', "警告", {
        confirmButtonText: "移除",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return delQuestion(id);
      }).then(() => {
        this.resetForm('examQuestionQueryForm');
        this.getExamQuestionList();
        this.msgSuccess("删除成功");
      }).catch(function() {});
    },

/* ################################# 下面是【试卷资源池】部分API ############################## */
    importFromPaperStore(){
      this.getPaperStoreList()
      this.paperStoreDialogOpen = true
    },

    /** 查询考核试卷基础信息列表 */
    getPaperStoreList() {
      this.paperStoreTableLoading = true;
      listPaper(this.paperStoreQueryParams).then(response => {
        this.paperStoreList = response.rows;
        this.paperStoreTotal = response.total;
        this.paperStoreTableLoading = false;
      });
    },

    /* 关闭试卷库 清理 */
    PaperStoreDialogClose(){
      this.paperStoreList = [];
      this.paperStoreTotal = 0;
      this.paperStoreDialogOpen = false
    },

    /** 搜索按钮操作 */
    handlePaperStoreQuery() {
      this.paperStoreQueryParams.pageNum = 1;
      this.getPaperStoreList();
    },


/* ################################# 下面是【题库】部分API ############################## */
    importFromQuestionStore(){
      this.getQuestionStoreList()
      this.questionStoreDialogOpen = true
    },

    /** 查询考试试卷题目列表 */
    getQuestionStoreList() {
      this.storeQuestionList = []
      this.questionStoreLoading = true;
      if (this.questionCategoryCodeArr && this.questionCategoryCodeArr.length){
        this.questionStoreQueryParams.questionCategoryCodes = this.questionCategoryCodeArr.join(",")
      } else {
        this.questionStoreQueryParams.questionCategoryCodes = null
      }

      storeQuestionListForExam(this.questionStoreQueryParams).then(response => {
        if (response.rows && response.rows.length){
          this.storeQuestionList = response.rows;
          for(let question of this.storeQuestionList){
            question.questionType = question.questionType.code
          }
        }
        this.storeQuestionTotal = response.total;
        this.questionStoreLoading = false;
      });
    },

    // 题目考核方向字典翻译
    questionCategoryCodeFormat(row, column) {
      return this.selectDictLabel(this.examModeOptions, row.questionCategoryCode);
    },

    // 试题类型字典翻译
    questionTypeFormat(questionType) {
      return this.selectDictLabel(this.questionTypeOptions, questionType);
    },

    /* 题库弹窗关闭回调 */
    storeQuestionDialogClose(){
      /* 已选中的题库试题ID */
      this.checkedStoreQuestionIds = null
      // 考试试卷题目表格数据
      this.storeQuestionList = []
      this.questionCategoryCodeArr = []
      // 查询参数
      this.questionStoreQueryParams = {
        pageNum: 1,
        pageSize: 10,
        examId: this.$route.params.examId,
        added: 'N',
        questionCategoryCodes: null,
        question: null,
        questionType: null,
        score: undefined
      }
    },

    // 题库试题多选框选中数据
    handleStoreQuestionSelectionChange(selection) {
      this.checkedStoreQuestionIds = selection.map(item => item.id).join(",")
    },

    /** 预览按钮操作 */
    handleQuestionView(row) {
      this.questionViewDialogOpen = true;
      this.storeQuestionModel = row;
      this.storeQuestionModel.answersView = row.answers || []
      this.storeQuestionModel.correctAnswer = row.correctAnswer || []
      this.storeQuestionReferredExams = []
      getExamQuestionReferredExams(row.id).then(res =>{
        this.storeQuestionReferredExams = res.data
      })
    },

    importByQuestion(storeQuestionId){
      this.exportProcess = 0
      importByQuestionIds({
        examId: this.examId,
        storeQuestionIds: storeQuestionId || this.checkedStoreQuestionIds
      }).then(res =>{
        if (res.code === 200) {
          this.questionStoreQueryParams.pageNum = 1;
          this.getQuestionStoreList();
          this.resetForm('questionStoreQueryForm');
          this.getExamQuestionList();
          this.exportProcess = 100
          this.msgSuccess("添加成功");
        }
      })
    },

    openPaperQuestionExportDialog(paperInfo){
      const _this = this
      _this.paperQuestionExportDialogOpen = true
      _this.selectedPaperInfo = paperInfo
      _this.paperPartPieLoading = false
    },

    /* 从试卷导入 */
    importByPaper(){
      const _this = this
      _this.exportProcess = 0
      _this.paperQuestionExportActiveStep = 3,
      _this.exportStatus = 'PROCESSING'
      importByPaperQuestion(_this.examId, _this.selectedPaperInfo.id).then(res =>{
        if (res.code === 200) {
          _this.getExamQuestionList();
          _this.exportStatus = 'SUCCESS'
          _this.exportProcess = 100
          _this.msgSuccess("导入成功");
        }
      }).catch(error => {
        _this.exportStatus = 'ERROR'
      })
    },

    /* 关闭[从试卷导入试题]弹窗回调 */
    paperQuestionExportDialogClose(){
      if (this.exportStatus == 'PROCESSING'){
        return this.msgWarning("导入未结束，请稍后");
      }
      this.exportStatus = null
      this.exportProcess = 0
      this.paperQuestionExportActiveStep = 1
      this.selectedPaperInfo = 1
      this.importByPaperQuestionProcessTimeLine = {}
      this.PaperStoreDialogClose()
      this.paperQuestionExportDialogOpen = false
    },

  },

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

  /* 堆叠动画 */
  .lay-container {
    margin-top: 60px;
    user-select: none;
    transform: translate3d(0, 0, 0);
    transform-style: preserve-3d;

    .lay-item {
      border: none;
      margin-top: -50px;
      position: relative;
      border-radius: 12px;
      color: white;
      transform: rotateX(-10deg) rotateY(0deg) rotate(0deg);
      transition: all 0.4s ease;
    }

    .lay-success{
      box-shadow: 20px 20px 60px rgba(34, 50, 84, 0.5), 1px 1px 0px 1px #3F58E3;
      background-image: linear-gradient(135deg, #7F94FC, #3F58E3);
    }

    .lay-warning{
      box-shadow: 20px 20px 60px rgba(34, 50, 84, 0.5), 1px 1px 0px 1px #c1a274;
      background-image: linear-gradient(135deg, #ccc56d, #b79e78);
    }

    .lay-fail{
      box-shadow: 20px 20px 60px rgba(34, 50, 84, 0.5), 1px 1px 0px 1px #b34827;
      background-image: linear-gradient(135deg, #da9580, #ca3434);
    }

    .lay-item:hover~ .lay-item {
      margin-top: -10px;
      transform: rotateX(-10deg) rotateY(0deg) rotate(-10deg) translate(10px, 30px);
    }

  }

</style>
