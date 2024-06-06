export interface IBoard {
  id?: string;
  title?: string;
  description?: string | null;
  activated?: boolean;
}

export const defaultValue: Readonly<IBoard> = {
  activated: false,
};
