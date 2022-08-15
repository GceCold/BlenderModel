package ltd.icecold.orangeengine.blender.animation;

public class AnimationPlayer {
    private float elapsedTime;
    private float prevElapsedTime;
    private boolean isEnd;
    private boolean doNotResetNext;
    private boolean reversed;
    private DynamicAnimation play;
    private TransformSheet actionAnimationCoord = new TransformSheet();

    public AnimationPlayer() {

    }

    public void update() {
        this.prevElapsedTime = this.elapsedTime;
        this.elapsedTime += 0.05F * 1.0F;

        if (this.elapsedTime >= this.play.getTotalTime()) {
            if (this.play.isRepeat()) {
                this.prevElapsedTime = 0;
                this.elapsedTime %= this.play.getTotalTime();
            } else {
                this.elapsedTime = this.play.getTotalTime();
                this.isEnd = true;
            }
        } else if (this.elapsedTime < 0) {
            if (this.play.isRepeat()) {
                this.prevElapsedTime = this.play.getTotalTime();
                this.elapsedTime = this.play.getTotalTime() + this.elapsedTime;
            } else {
                this.elapsedTime = 0;
                this.isEnd = true;
            }
        }
    }

    public void synchronize(AnimationPlayer animationPlayer) {
        this.play = animationPlayer.play;
        this.elapsedTime = animationPlayer.elapsedTime;
        this.prevElapsedTime = animationPlayer.prevElapsedTime;
        this.isEnd = animationPlayer.isEnd;
    }

    public void resetPlayer() {
        this.elapsedTime = 0;
        this.prevElapsedTime = 0;
        this.isEnd = false;
    }

    public void setPlayAnimation(DynamicAnimation animation) {
        if (this.doNotResetNext) {
            this.doNotResetNext = false;
        } else {
            this.resetPlayer();
        }

        this.play = animation;
    }


    public TransformSheet getActionAnimationCoord() {
        return this.actionAnimationCoord;
    }


    public Pose getCurrentPose(float partialTicks) {
        return this.play.getPoseByTime(this.prevElapsedTime + (this.elapsedTime - this.prevElapsedTime) * partialTicks, partialTicks);
    }

    public float getElapsedTime() {
        return this.elapsedTime;
    }

    public float getPrevElapsedTime() {
        return this.prevElapsedTime;
    }

    public void setElapsedTime(float elapsedTime) {
        this.elapsedTime = elapsedTime;
        this.prevElapsedTime = elapsedTime;
        this.isEnd = false;
    }

    public DynamicAnimation getPlay() {
        return this.play;
    }

    public void markToDoNotReset() {
        this.doNotResetNext = true;
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public void setReversed(boolean reversed) {
        if (reversed != this.reversed) {
            this.setElapsedTime(this.getPlay().getTotalTime() - this.getElapsedTime());
            this.reversed = reversed;
        }
    }

}