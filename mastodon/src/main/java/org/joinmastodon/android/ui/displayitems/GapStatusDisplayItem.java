package org.joinmastodon.android.ui.displayitems;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joinmastodon.android.R;
import org.joinmastodon.android.ui.drawables.SawtoothTearDrawable;

// Mind the gap!
public class GapStatusDisplayItem extends StatusDisplayItem{
	public boolean loading;
	public boolean enteredFromTop; // While the user was scrolling, did the gap item pop out from behind the top edge of the list?
	public boolean visible; // Is this item currently within the viewport of the RecyclerView (and has a bound view)?

	public GapStatusDisplayItem(String parentID, Callbacks callbacks, Context context){
		super(parentID, callbacks, context);
	}

	@Override
	public Type getType(){
		return Type.GAP;
	}

	public static class Holder extends StatusDisplayItem.Holder<GapStatusDisplayItem>{
		public final ProgressBar progress;
		public final TextView text;

		public Holder(Context context, ViewGroup parent){
			super(context, R.layout.display_item_gap, parent);
			progress=findViewById(R.id.progress);
			text=findViewById(R.id.text);
			itemView.setForeground(new SawtoothTearDrawable(context));
		}

		@Override
		public void onBind(GapStatusDisplayItem item){
			text.setVisibility(item.loading ? View.GONE : View.VISIBLE);
			progress.setVisibility(item.loading ? View.VISIBLE : View.GONE);
			text.setText(item.enteredFromTop ? R.string.load_missing_posts_above : R.string.load_missing_posts_below);
		}

		@Override
		public void onClick(){
			item.callbacks.onGapClick(this);
		}
	}
}
