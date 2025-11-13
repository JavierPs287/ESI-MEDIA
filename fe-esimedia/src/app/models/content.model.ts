export interface Content {
  title: string;
  description?: string;
  tags?: string[];
  duration?: number;
  vip?: boolean;
  visible?: boolean;
  visibilityChangeDate?: Date;
  visibilityDeadline?: Date;
  minAge?: number;
  imageId: number;
  creador: string;
  rating?: number;
  views?: number;
}