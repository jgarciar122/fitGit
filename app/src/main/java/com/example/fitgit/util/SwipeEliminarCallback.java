package com.example.fitgit.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SwipeEliminarCallback extends ItemTouchHelper.SimpleCallback {

    private final Drawable iconoPapelera;
    private final Paint fondoRojo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float cornerRadius;

    public SwipeEliminarCallback(Context context) {
        super(0, ItemTouchHelper.LEFT);
        iconoPapelera = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
        fondoRojo.setColor(Color.parseColor("#FF5252"));
        cornerRadius = 24f; // coincide con cardCornerRadius del item
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;

        if (dX < 0) {
            float left = itemView.getRight() + dX;
            float top = itemView.getTop();
            float right = itemView.getRight();
            float bottom = itemView.getBottom();

            // Fondo rojo con esquinas redondeadas solo en la derecha
            RectF rect = new RectF(left, top, right, bottom);
            c.drawRoundRect(rect, cornerRadius, cornerRadius, fondoRojo);

            // Cuadrar la esquina izquierda del fondo (para que no se vea redondo a la izquierda)
            c.drawRect(left, top, left + cornerRadius, bottom, fondoRojo);

            // Icono papelera centrado en el área roja
            if (iconoPapelera != null) {
                int iconSize = dpToPx(itemView.getContext(), 24);
                int margenDerecha = dpToPx(itemView.getContext(), 24);
                int iconTop = (int) (top + (bottom - top - iconSize) / 2f);
                int iconLeft = (int) (right - margenDerecha - iconSize);
                int iconRight = (int) (right - margenDerecha);
                int iconBottom = iconTop + iconSize;

                iconoPapelera.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                iconoPapelera.setTint(Color.WHITE);
                iconoPapelera.draw(c);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
