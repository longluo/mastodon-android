package org.joinmastodon.android.ui.displayitems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joinmastodon.android.R;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.ui.OutlineProviders;
import org.joinmastodon.android.ui.drawables.SpoilerStripesDrawable;
import org.joinmastodon.android.ui.drawables.TiledDrawable;
import org.joinmastodon.android.ui.text.HtmlParser;
import org.joinmastodon.android.ui.utils.CustomEmojiHelper;

import java.util.ArrayList;

import me.grishka.appkit.imageloader.ImageLoaderViewHolder;
import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;
import me.grishka.appkit.utils.V;

public class SpoilerStatusDisplayItem extends StatusDisplayItem{
	public final Status status;
	public final ArrayList<StatusDisplayItem> contentItems=new ArrayList<>();
	public final Status.SpoilerType spoilerType;
	private final CharSequence parsedTitle;
	private CharSequence translatedTitle;
	private final CustomEmojiHelper emojiHelper;
	public final Type type;

	public SpoilerStatusDisplayItem(String parentID, Callbacks callbacks, Context context, String title, Status status, Status statusForContent, Type type, Status.SpoilerType spoilerType){
		super(parentID, callbacks, context);
		this.status=status;
		this.type=type;
		this.spoilerType=spoilerType;
		if(TextUtils.isEmpty(title)){
			parsedTitle=HtmlParser.parseCustomEmoji(statusForContent.spoilerText, statusForContent.emojis);
			emojiHelper=new CustomEmojiHelper();
			emojiHelper.setText(parsedTitle);
		}else{
			parsedTitle=title;
			emojiHelper=null;
		}
	}

	@Override
	public int getImageCount(){
		return emojiHelper==null ? 0 : emojiHelper.getImageCount();
	}

	@Override
	public ImageLoaderRequest getImageRequest(int index){
		return emojiHelper.getImageRequest(index);
	}

	@Override
	public Type getType(){
		return type;
	}

	public static class Holder extends StatusDisplayItem.Holder<SpoilerStatusDisplayItem> implements ImageLoaderViewHolder{
		private final TextView title, action;
		private final View button;

		public Holder(Context context, ViewGroup parent, Type type){
			super(context, R.layout.display_item_spoiler, parent);
			title=findViewById(R.id.spoiler_title);
			action=findViewById(R.id.spoiler_action);
			button=findViewById(R.id.spoiler_button);

			button.setOutlineProvider(OutlineProviders.roundedRect(8));
			button.setClipToOutline(true);
			LayerDrawable spoilerBg=(LayerDrawable) button.getBackground().mutate();
			if(type==Type.SPOILER){
				spoilerBg.setDrawableByLayerId(R.id.left_drawable, new SpoilerStripesDrawable(true));
				spoilerBg.setDrawableByLayerId(R.id.right_drawable, new SpoilerStripesDrawable(false));
			}else if(type==Type.FILTER_SPOILER){
				Drawable texture=context.getDrawable(R.drawable.filter_banner_stripe_texture);
				spoilerBg.setDrawableByLayerId(R.id.left_drawable, new TiledDrawable(texture));
				spoilerBg.setDrawableByLayerId(R.id.right_drawable, new TiledDrawable(texture));
			}
			button.setBackground(spoilerBg);
			button.setOnClickListener(v->item.callbacks.onRevealSpoilerClick(this));
		}

		@Override
		public void onBind(SpoilerStatusDisplayItem item){
			itemView.setPaddingRelative(V.dp(item.fullWidth ? 16 : 64), itemView.getPaddingTop(), itemView.getPaddingEnd(), itemView.getPaddingBottom());
			if(item.status.translationState==Status.TranslationState.SHOWN){
				if(item.translatedTitle==null){
					item.translatedTitle=item.status.translation.spoilerText;
				}
				title.setText(item.translatedTitle);
			}else{
				title.setText(item.parsedTitle);
			}
			action.setText(item.status.revealedSpoilers.contains(item.spoilerType) ? R.string.spoiler_hide : R.string.spoiler_show);
		}

		@Override
		public void setImage(int index, Drawable image){
			item.emojiHelper.setImageDrawable(index, image);
			title.invalidate();
		}

		@Override
		public void clearImage(int index){
			setImage(index, null);
		}
	}
}
