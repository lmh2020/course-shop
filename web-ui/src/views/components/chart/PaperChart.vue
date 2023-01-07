<template>

  <el-row class="panel-group" :paperId="paperId" :searchMode="searchMode">
    <el-col v-if="searchMode.includes('questionPart')" v-loading="paperPartPieLoading" :xs="24" :sm="24" :lg="grid" class="card-panel-col">
      <div class="card-panel">
        <el-row>
          <el-col :span="4">
            <div class="card-panel-icon-wrapper icon-people">
              <svg-icon icon-class="tree" class-name="card-panel-icon" />
            </div>
          </el-col>
          <el-col :span="16">
            <div ref="paperPartPieChart" class="chart"/>
          </el-col>
          <el-col :span="4">
            <div class="card-panel-description">
              <div class="card-panel-text">试题总数</div>
              <count-to :start-val="0" :end-val="paperQuestionCount" :duration="3000" class="card-panel-num" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">总分</div>
              <count-to :start-val="0" :end-val="paperTotalScore" :duration="3000" class="card-panel-num" />
            </div>
          </el-col>
        </el-row>
      </div>
    </el-col>
    <el-col v-if="searchMode.includes('examPaperDatePlan')" v-loading="paperExamPlanLoading" :xs="24" :sm="24" :lg="grid" class="card-panel-col">
      <div class="card-panel">
        <el-row>
          <el-col :span="4">
            <div class="card-panel-icon-wrapper icon-people">
              <svg-icon icon-class="date" class-name="card-panel-icon" />
            </div>
          </el-col>
          <el-col :span="16">
            <div ref="paperExamPlanBarChart" class="chart"/>
          </el-col>
          <el-col :span="4">
            <div class="card-panel-description">
              <div class="card-panel-text">近期考核</div>
              <count-to :start-val="0" :end-val="recentlyExamCount" :duration="3000" class="card-panel-num" />
            </div>
          </el-col>
        </el-row>
      </div>
    </el-col>
    <el-col v-if="searchMode.includes('examPassRate')" v-loading="paperExamStatisticsGaugeLoading" :xs="24" :sm="24" :lg="grid" class="card-panel-col">
      <div class="card-panel">
        <el-row>
          <el-col :span="4">
            <div class="card-panel-icon-wrapper icon-people">
              <svg-icon icon-class="size" class-name="card-panel-icon" />
            </div>
          </el-col>
          <el-col :span="16">
            <div ref="paperExamStatisticsGaugeChart" class="chart"/>
          </el-col>
          <el-col :span="4">
            <div class="card-panel-description">
              <div class="card-panel-text">考核人数</div>
              <count-to :start-val="0" :end-val="totalExamPeopleCount" :duration="3000" class="card-panel-num" />
            </div>
            <div class="card-panel-description">
              <div class="card-panel-text">及格人数</div>
              <count-to :start-val="0" :end-val="totalExamPassedCount" :duration="3000" class="card-panel-num" />
            </div>
          </el-col>
        </el-row>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import {statisticsPaperInfo} from "@/api/edu/paper";
import echarts from 'echarts'
import CountTo from 'vue-count-to'
import PieChart from "@/views/components/chart/PieChart";
require('echarts/theme/macarons') // echarts theme

export default {
  components: {
    PieChart,
    CountTo,
  },
  props: {
    paperId: {
    },
    searchMode: {
      type: Array,
      // default: () => ['questionPart', 'examPaperDatePlan', 'examPassRate']
      default: () => ['questionPart']
    },
  },
  data() {
    return {
      chart: {
        paperPartPieChart: null,
        paperExamPlanBarChart: null,
        paperExamStatisticsGaugeChart: null
      },
      paperQuestionCount: 0,
      paperTotalScore: 0,
      recentlyExamCount: 0,
      totalExamPeopleCount: 0,
      totalExamPassedCount: 0,

      paperPartPieLoading: false,
      paperExamPlanLoading: false,
      paperExamStatisticsGaugeLoading: false,

      grid: 24 / this.searchMode.length
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart(this.searchMode)
    })
  },
  beforeDestroy() {
    for(let key in this.chart){
      let chart = this.chart[key]
      if (chart){
        chart.dispose()
        this.chart[key] = null
      }
    }
  },
  methods: {

    /* 加载Echarts数据 */
    initChart(searchMode){
      this.paperPartPieLoading = searchMode.includes('questionPart')
      this.paperExamPlanLoading = searchMode.includes('examPaperDatePlan')
      this.paperExamStatisticsGaugeLoading = searchMode.includes('examPassRate')
      statisticsPaperInfo(this.paperId, searchMode).then((res) => {
        if (res.code == 200){
          const data = res.data
          if (searchMode.includes('questionPart')){
            this.initPaperPartPieChart(data.questionParts)
            this.paperPartPieLoading = false
          }
          if (searchMode.includes('examPaperDatePlan')){
            this.initPaperExamPlanBarChart(data.examPaperDatePlans)
            this.paperExamPlanLoading = false
          }
          if (searchMode.includes('examPassRate')){
            this.initPaperExamStatisticsGaugeChart(data.totalExamPeopleCount, data.totalExamPassedCount)
            this.paperExamStatisticsGaugeLoading = false
          }
        }
      })
    },

    /* 试卷的试题类型分布饼图 */
    initPaperPartPieChart(questionParts) {
      this.paperQuestionCount = 0
      this.paperTotalScore = 0
      if (questionParts && questionParts.length){
        const seriesData = questionParts.map(part => {
          this.paperQuestionCount += part.questionCount
          this.paperTotalScore += part.questionScore
          return {
            value: part.questionCount,
            name: part.questionType.desc
          }
        })
        this.chart.paperPartPieChart = echarts.init(this.$refs.paperPartPieChart, 'macarons')
        this.chart.paperPartPieChart.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '{b} : <br/>{c}道 ({d}%)'
          },
          series: [
            {
              name: '试题数量',
              type: 'pie',
              radius: '60%',
              data: seriesData,
            }
          ]
        })
      }
    },

    /* 引用了该试卷的考核时间安排 */
    initPaperExamPlanBarChart(examPaperDatePlans){
      this.recentlyExamCount = 0
      if (examPaperDatePlans && examPaperDatePlans.length){
        const seriesData = []
        const xAxisData = []
        for (let datePlan of examPaperDatePlans){
          this.recentlyExamCount += datePlan.examCount
          xAxisData.push(datePlan.examDate)
          seriesData.push({
            value: datePlan.examCount,
            name: datePlan.title
          })
        }
        this.chart.paperExamPlanBarChart = echarts.init(this.$refs.paperExamPlanBarChart, 'macarons')
        this.chart.paperExamPlanBarChart.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '当天考试场次 : <br/>{c}场'
          },
          xAxis: {
            type: 'category',
            data: xAxisData
          },
          yAxis: {
            type: 'value',
            minInterval: 1,
            min: 0,
            max: 10,
          },
          series: [{
            data: seriesData,
            type: 'bar',
            center: ['50%', '25%'],
          }]
        })
      }

    },

    /* 该试卷的各项统计 */
    initPaperExamStatisticsGaugeChart(totalExamPeopleCount, totalExamPassedCount){
      this.totalExamPeopleCount = totalExamPeopleCount
      this.totalExamPassedCount = totalExamPassedCount
      const passedRate = totalExamPeopleCount ? parseInt(totalExamPassedCount * 100 / totalExamPeopleCount) : null
      this.chart.paperExamStatisticsGaugeChart = echarts.init(this.$refs.paperExamStatisticsGaugeChart, 'macarons')
      this.chart.paperExamStatisticsGaugeChart.setOption({
        tooltip: {
          formatter: '{a} <br/>{b} : {c}%'
        },
        series: [
          {
            name: '近期统计',
            type: 'gauge',
            detail: {formatter: '{value}%'},
            data: [{value: passedRate, name: '及格率'}]
          }
        ]
      })
    },

  }
}
</script>


<style lang="scss" scoped>

  .panel-group {
    margin-top: 18px;

    .card-panel-col {
      margin-bottom: 32px;
    }

    .card-panel {
      height: 250px;
      font-size: 12px;
      position: relative;
      overflow: hidden;
      color: #666;
      background: #fff;
      box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
      border-color: rgba(0, 0, 0, .05);

      .el-row {
        height: 100%;
        .el-col {
          height: 100%;
        }
      }

      .chart {
        width: 100%;
        height: 100%;
      }

      .icon-people {
        color: #40c9c6;
      }

      .icon-message {
        color: #36a3f7;
      }

      .icon-money {
        color: #f4516c;
      }

      .icon-shopping {
        color: #34bfa3
      }

      .card-panel-icon-wrapper {
        float: left;
        margin: 14px 0 0 14px;
        padding: 16px;
        transition: all 0.38s ease-out;
        border-radius: 6px;
      }

      .card-panel-icon {
        float: left;
        font-size: 48px;
      }

      .card-panel-description {
        //float: right;
        font-weight: 900;
        margin: 26px;
        margin-left: 0px;

        .card-panel-text {
          line-height: 18px;
          color: rgba(0, 0, 0, 0.45);
          font-size: 16px;
          margin-bottom: 12px;
        }

        .card-panel-num {
          font-size: 20px;
        }
      }
    }
  }

  @media (max-width:550px) {
    .card-panel-description {
      display: none;
    }

    .card-panel-icon-wrapper {
      float: none !important;
      width: 100%;
      height: 100%;
      margin: 0 !important;

      .svg-icon {
        display: block;
        margin: 14px auto !important;
        float: none !important;
      }
    }
  }

</style>
