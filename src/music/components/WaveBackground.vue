<template>
  <div class="alien-landscape-container" ref="container">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script>
export default {
  name: 'AlienLandscape',
  mounted() {
    this.initAlienLandscape();
  },
  beforeUnmount() {
    cancelAnimationFrame(this.animationFrame);
  },
  methods: {
    initAlienLandscape() {
      const container = this.$refs.container;
      const canvas = this.$refs.canvas;

      // 设置容器样式
      container.style.position = 'fixed'; // 使用 fixed 定位
      container.style.width = '100vw';
      container.style.height = '100vh';
      container.style.overflow = 'hidden';
      container.style.background = '#000';
      container.style.top = '0';
      container.style.left = '0';
      container.style.zIndex = '-1'; // 确保背景在内容下方

      // 设置画布样式
      canvas.style.position = 'absolute';
      canvas.style.transform = 'translate(-50%, 0%)';
      canvas.style.left = '50%';
      canvas.style.height = '100%';
      canvas.style.width = '100%';

      // 从原代码中提取核心逻辑
      this.setupAlienLandscape(canvas);
    },

    setupAlienLandscape(canvas) {
      // 原代码逻辑保持不变
      const t = canvas;
      const e = (t=1,e=0)=>e + (t - e) * Math.random()
          , i = (t,e=0,i=1)=>t < e ? e : t > i ? i : t;
      let r = 1;
      const a = (t=1,e=0)=>(r ^= r << 13,
          r ^= r >>> 17,
          r ^= r << 5,
      e + (t - e) * Math.abs(r % 1e9) / 1e9);

      class o {
        constructor(t=1, e=1, i=1, r=1) {
          this.r = t,
              this.g = e,
              this.b = i,
              this.a = r
        }
        copy() {
          return new o(this.r,this.g,this.b,this.a)
        }
        add(t) {
          return new o(this.r + t.r,this.g + t.g,this.b + t.b,this.a + t.a)
        }
        subtract(t) {
          return new o(this.r - t.r,this.g - t.g,this.b - t.b,this.a - t.a)
        }
        multiply(t) {
          return new o(this.r * t.r,this.g * t.g,this.b * t.b,this.a * t.a)
        }
        divide(t) {
          return new o(this.r / t.r,this.g / t.g,this.b / t.b,this.a / t.a)
        }
        scale(t, e=t) {
          return new o(this.r * t,this.g * t,this.b * t,this.a * e)
        }
        clamp() {
          return new o(i(this.r),i(this.g),i(this.b),i(this.a))
        }
        lerp(t, e) {
          return this.add(t.subtract(this).scale(i(e)))
        }
        setHSLA(t=0, e=0, i=1, r=1) {
          const a = i < .5 ? i * (1 + e) : i + e - i * e
              , o = 2 * i - a
              , l = (t,e,i)=>(i = (i % 1 + 1) % 1) < 1 / 6 ? t + 6 * (e - t) * i : i < .5 ? e : i < 2 / 3 ? t + (e - t) * (2 / 3 - i) * 6 : t;
          return this.r = l(o, a, t + 1 / 3),
              this.g = l(o, a, t),
              this.b = l(o, a, t - 1 / 3),
              this.a = r,
              this
        }
        mutate(t=.05, e=0) {
          return new o(this.r + a(t, -t),this.g + a(t, -t),this.b + a(t, -t),this.a + a(e, -e)).clamp()
        }
        rgba() {
          return `rgb(${255 * this.r | 0},${255 * this.g | 0},${255 * this.b | 0},${this.a})`
        }
        rgbaInt() {
          return (255 * this.r | 0) + (255 * this.g << 8) + (255 * this.b << 16) + (255 * this.a << 24)
        }
      }

      let l, s, n, h, d, g, c, b, p, u;
      const w = () => {
        l = 0 | e(1e9),
            s = e(),
            n = (new o).setHSLA(s, e(), e(.5)),
            h = (new o).setHSLA(s + e(.3, .7), e(), e(.8, .2)),
            d = e(1e3),
            g = ((t=new o,i=new o(0,0,0,1),r)=>r ? t.lerp(i, e()) : new o(e(t.r, i.r),e(t.g, i.g),e(t.b, i.b),e(t.a, i.a)))(new o(.1,.1,.1), new o(.9,.9,.9)),
            c = e(12, 1) * (e() < .5 ? 1 : -1),
            b = e() < .5,
            p = e() < .5,
            u = e() < .03 ? 0 : 1 + e() ** 2 * 5 | 0
      }
      w();

      // 添加点击事件
      canvas.onclick = () => w();

      let f = 0
          , m = 0
          , S = 0
          , C = t.getContext("2d")
          , y = (e=>{
        this.animationFrame = requestAnimationFrame(y);
        e < S - 3 || (S = Math.max(S + 1e3 / 60, e),
            f = m++ / 60,
            function(e) {
              t.width = t.clientWidth;
              t.height = t.clientHeight;
              r = l;
              const i = 600 + a(300)
                  , s = C.fillStyle = C.createLinearGradient(0, 0, 0, i);
              s.addColorStop(0, n.rgba()),
                  s.addColorStop(1, h.rgba()),
                  C.fillRect(0, 0, t.width, t.height),
                  C.globalCompositeOperation = "lighter";
              for (let r = 4e3; r--; ) {
                const r = a(2, 1)
                    , l = (new o).setHSLA(a(), a() ** 3, a() ** 2)
                    , s = (a(t.width) + a(9) * e) % (t.width + 20) - 10
                    , n = a(i);
                C.fillStyle = l.rgba(),
                    C.fillRect(s, n, r, r)
              }
              if (C.globalCompositeOperation = "source-over",
                  u) {
                let e = r;
                for (let l = 2; l--; ) {
                  r = e,
                      C.globalCompositeOperation = l ? "source-over" : "lighter";
                  for (let e = u; e--; ) {
                    const r = e ? a() ** 2 * 5 + 9 : a() ** 3 * 30 + 20
                        , s = (new o).setHSLA(a(), a(), a(.5, 1))
                        , n = a(t.width)
                        , h = a(i - 300);
                    if (l) {
                      let e = C.fillStyle = C.createRadialGradient(n, h, 0, n, h, r);
                      e.addColorStop(0, s.rgba()),
                          e.addColorStop(.8, s.mutate(.3).rgba()),
                          e.addColorStop(1, s.mutate(.3).scale(1, 0).rgba()),
                          C.fillRect(0, 0, t.width, t.height)
                    } else {
                      let e = C.fillStyle = C.createRadialGradient(n, h, 0, n, h, 400 * r);
                      e.addColorStop(0, s.scale(.3, 1).rgba()),
                          e.addColorStop(.1, s.scale(.1, 1).rgba()),
                          e.addColorStop(1, s.scale(0, 1).rgba()),
                          C.fillRect(0, 0, t.width, t.height)
                    }
                  }
                }
                C.globalCompositeOperation = "source-over"
              }
              let w = a() > .5 ? -1 : 1;
              for (let e = p ? 100 : 20; e--; ) {
                const e = a(4, 1)
                    , r = a(99, p ? 19 : 9)
                    , l = (new o).setHSLA(a(), a() ** 7, a(1, .5))
                    , s = t.width + 1e3
                    , n = i + 1e3
                    , h = !p || a() < .02 ? a(-99, 99) : (r + a(-10, 10)) * w
                    , d = (a(s) + f * h) % s - 500
                    , g = (a(n) + f * r) % n - 500
                    , c = a(.5, 3)
                    , b = d - c * h
                    , u = g - c * r
                    , m = (new o).setHSLA(a(), a() ** 9, a(.1, .5))
                    , S = C.strokeStyle = C.createLinearGradient(d, g, b, u);
                S.addColorStop(0, m.rgba()),
                    S.addColorStop(1, m.scale(1, 0).rgba()),
                    C.globalCompositeOperation = "lighter",
                    C.lineWidth = e,
                    C.lineTo(d, g),
                    C.lineTo(b, u),
                    C.stroke(),
                    C.beginPath(),
                    C.globalCompositeOperation = "source-over",
                    C.fillStyle = l.rgba(),
                    C.arc(d, g, e / 2, 0, 9),
                    C.fill(),
                    C.beginPath()
              }
              let m = g;
              for (let r = 0; ++r < 8; ) {
                const l = a(.3, 1);
                let s = 0
                    , h = 0
                    , g = r * r * 1e3 + e * c * (r - 1) ** 2
                    , p = 0 | g
                    , u = g % 1 - 1
                    , w = i - 250 + r * r * 13;
                for (let t = 3e3; t--; ) {
                  let e = 10 * Math.sin((++p + d) ** 2);
                  Math.abs(e) < 1 && (s = e * (b ? 3 : 1.5) * l),
                      h += s -= h / 2e3,
                      C.lineTo((t + u - 1) * r, h * r ** .7 / 2 + w)
                }
                const f = m.lerp(n, 1 - .2 * r)
                    , S = C.fillStyle = C.createLinearGradient(0, w - 100, 0, w + 300);
                S.addColorStop(0, f.clamp().rgba()),
                    S.addColorStop(1, f.subtract(new o(1,1,1,0)).mutate(.4).clamp().rgba()),
                    m = m.mutate(a() ** 2 * .3),
                    C.lineTo(0, t.height),
                    C.lineTo(t.width, t.height),
                    C.fill(),
                    C.beginPath()
              }
            }(f))
      });
      y();
    },
  }
}
</script>

<style scoped>
.alien-landscape-container {
  position: fixed; /* 使用 fixed 定位 */
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: -1; /* 确保背景在内容下方 */
  pointer-events: none; /* 可选：防止背景拦截点击事件 */
}
</style>
