<template>
  <div>
    <div v-if="!done" class="counter">
      <div class="nums">
        <span v-for="(item, index) in buildNum" :key="index" :class="item.class">{{ item.num }}</span>
      </div>
      <h4>考试即将结束</h4>
    </div>
    <div v-else>
      <h4>考试已结束！</h4>
    </div>
  </div>
</template>

<script>

export default {
  components: {},
  props: {
    times: {
      type: Number,
    }
  },
  data() {
    return {
      done: false
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.runAnimation()
    })
  },
  computed: {
    buildNum() {
      let tims = []
      for (let i = this.times || 0; i >= 0; i--) {
        tims.push({
          num: i,
          class: ''
        })
      }
      tims[0].class = 'in'
      return tims
    }
  },
  methods: {
    /* TODO 待优化，上面倒计时元素个数与倒计时时间挂钩，可能会导致生成过多元素影响页面加载。 */
    runAnimation() {
      const _this = this
      const numItems = document.querySelectorAll('.nums span')
      numItems.forEach((num, idx) => {
        const penultimate = numItems.length - 1;
        num.addEventListener('animationend', (e) => {
          if (e.animationName.startsWith('goIn') && idx !== penultimate) {
            num.classList.remove('in');
            num.classList.add('out');
          } else if (e.animationName.startsWith('goOut') && num.nextElementSibling) {
            num.nextElementSibling.classList.add('in');
          } else {
            document.querySelector('.counter').classList.add('hide');
            _this.done = true
          }
        });
      });
    }

  }
}
</script>


<style lang="scss" scoped>

.counter {
  //height: 100px;
  //position: fixed;
  //top: 50%;
  //left: 50%;
  //transform: translate(-50%, -50%);
  text-align: center;
}

.counter.hide {
  transform: translate(-50%, -50%) scale(0);
  animation: hide .2s ease-out;
}

@keyframes hide {
  0% {
    transform: translate(-50%, -50%) scale(1);
  }

  100% {
    transform: translate(-50%, -50%) scale(0);
  }
}

@keyframes show {
  0% {
    transform: translate(-50%, -50%) scale(0);
  }

  80% {
    transform: translate(-50%, -50%) scale(1.4);
  }

  100% {
    transform: translate(-50%, -50%) scale(1);
  }
}

.nums {
  color: #3498db;
  position: relative;
  font-size: 55px;
  overflow: hidden;
  width: 250px;
  height: 50px;
}

.nums span {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%) rotate(120deg);
  transform-origin: bottom center;
  padding: 30px;
}

.nums span.in {
  transform: translate(-50%, -50%) rotate(0deg);
  animation: goIn .5s ease-in-out;
}

.nums span.out {
  animation: goOut .5s ease-in-out;
}

@keyframes goIn {
  0% {
    transform: translate(-50%, -50%) rotate(120deg);
  }
  30% {
    transform: translate(-50%, -50%) rotate(-20deg);
  }

  60% {
    transform: translate(-50%, -50%) rotate(10deg);
  }

  90%, 100% {
    transform: translate(-50%, -50%) rotate(0deg);
  }

}

@keyframes goOut {
  0%, 30% {
    transform: translate(-50%, -50%) rotate(0deg);
  }

  60% {
    transform: translate(-50%, -50%) rotate(20deg);
  }

  100% {
    transform: translate(-50%, -50%) rotate(-120deg);
  }
}

h4 {
  font-size: 20px;
  margin: 5px;
  text-transform: uppercase;
}


footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  letter-spacing: 1px;
}

footer i {
  color: red;
}

footer a {
  color: #3C97BF;
  text-decoration: none;
}

</style>
