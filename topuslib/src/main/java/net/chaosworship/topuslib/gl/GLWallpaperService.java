package net.chaosworship.topuslib.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.AttributeSet;
import android.view.SurfaceHolder;


public abstract class GLWallpaperService extends WallpaperService {

    @Override
    public abstract Engine onCreateEngine();

    public abstract class GLEngine extends Engine {

        public class WallpaperGLSurfaceView extends GLSurfaceView {

            public WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            public WallpaperGLSurfaceView(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            @Override
            public SurfaceHolder getHolder() {
                return GLEngine.this.getSurfaceHolder();
            }

            public void onDestroy() {
                super.onDetachedFromWindow();
            }
        }

        private WallpaperGLSurfaceView mGLSurfaceView;

        public GLEngine() {
            super();
            mGLSurfaceView = null;
        }

        public void requestRender() {
            mGLSurfaceView.requestRender();
        }

        public abstract void start();

        public abstract void stop();

        protected void setRenderer(GLSurfaceView.Renderer renderer) {
            mGLSurfaceView.setRenderer(renderer);
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            mGLSurfaceView = this.new WallpaperGLSurfaceView(GLWallpaperService.this);
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setPreserveEGLContextOnPause(false);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if(visible) {
                mGLSurfaceView.onResume();
                start();
            } else {
                stop();
                mGLSurfaceView.onPause();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            stop();
            mGLSurfaceView.onDestroy();
            mGLSurfaceView = null;
        }
    }
}
